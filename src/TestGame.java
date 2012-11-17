import csv.CSVWriter;

import descriptors.Dominance;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import game.*;

import map.MapGenerator;

import td.TDLearning;
import td.TDPlayer;

import ui.terminal.TerminalUI;
import ui.terminal.TerminalPlayer;

import util.Configuration;

public class TestGame 
{
	static private class Experiment extends GameEventAdapter implements Runnable
	{
		Configuration config;

		CSVWriter writer;

		TDLearning brain;

		TDPlayer tdPlayer;

		List<Player> players;

		MapGenerator generator;

		HashMap<Player, Integer> scores;

		Game game;

		public Experiment(Configuration config, CSVWriter writer)
		{
			this.config = config;

			this.writer = writer;

			brain = new TDLearning(config.getSection("network"));

			tdPlayer = new TDPlayer("TD", brain, config.getSection("player"));

			players = new Vector<Player>();
			players.add(tdPlayer);
			players.add(new RandomPlayer("Random"));
			players.add(new SimplePlayer("Simple"));
		
			generator = new MapGenerator(players);
		}

		public void onTurnEnded(GameState state)
		{
			// No need to continue playing if TDPlayer is out of the game.
			if (!state.getPlayers().contains(tdPlayer))
				game.stop();
		}
		
		public void onGameEnded(GameState state)
		{
			Player winner = state.getCountries().get(0).getPlayer();
			scores.put(winner, scores.get(winner) + 1);
		}

		public void run()
		{
			resetScores();

			writeHeaders();	

			int epochs = config.getInt("epochs", 2000);
			for (int i = 1; i <= epochs; ++i)
			{
				// Generate a random map
				GameState state = generator.generate(4, 2.5);
				
				game = new Game(players, state);
				game.addEventListener(this);

				game.run();

				if (i % 10 == 0)
					writeScores(i);
			}
		}

		private void resetScores()
		{
			scores = new HashMap<Player, Integer>();
			for (Player player : players)
				scores.put(player, 0);
		}

		private void writeHeaders()
		{
			writer.write("Round");
			writer.write(players);
			writer.write("error");
			writer.write("variance");
			writer.endLine();
		}

		private void writeScores(int i)
		{
			writer.write(i);
			
			for (Player player : players)
				writer.write(scores.get(player));

			writer.write(brain.getError().mean());
			writer.write(brain.getError().variance());
			writer.endLine();
		}
	}

	static public void main(String[] args) throws Exception
	{
		if (args.length < 2)
		{
			usage();
			return;
		}

		// Read default configuration
		File defaultConfigFile = new File(args[0]);
		Configuration defaultConfig = new Configuration();
		defaultConfig.read(defaultConfigFile);

		ExecutorService queue = Executors.newFixedThreadPool(8);
		List<Future> results = new ArrayList<Future>();

		// For each other configuration file provided, create a few experiments
		for (int i = 1; i < args.length; ++i)
		{
			File configFile = new File(args[i]);

			// Skip files that do not exist
			if (!configFile.exists())
			{
				System.err.println("Error: File " + args[i] + " does not exist");
				continue;
			}

			// Derive a configuration from the default configuration
			Configuration config = new Configuration(defaultConfig);
			config.read(configFile);

			// Set up experiment
			for (int run = 0; run < config.getInt("runs", 20); ++run)
			{
				File outputFile = new File(args[i] + "-run-" + run + ".csv");
				CSVWriter output = new CSVWriter(new PrintStream(outputFile));

				Experiment experiment = new Experiment(config, output);

				// Submit experiment to the executer
				results.add(queue.submit(experiment));
			}
		}

		// No more new experiments will be added.
		queue.shutdown();
		
		// While the experiments are run, print the status of said experiments.
		while (queue.awaitTermination(1, TimeUnit.SECONDS) == false)
		{
			int finishedTasks = 0;
			for (Future task : results)
				if (task.isDone())
					finishedTasks++;

			System.out.println("Finished " + finishedTasks + " of " + results.size() + " tasks");
		}
	}

	static public void usage()
	{
		System.out.println("Usage: TestGame default.conf test1.conf test2.conf ...");
	} 
}
