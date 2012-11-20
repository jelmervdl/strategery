import csv.CSVWriter;

import descriptors.Dominance;

import java.util.*;
import java.util.concurrent.Callable;
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
import util.Instrument;

public class TestGame 
{
	static private class Experiment extends GameEventAdapter implements Callable<Double>
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
			// Note that this might make the total scores table incomplete.
			if (!state.getPlayers().contains(tdPlayer))
				game.stop();
		}
		
		public void onGameEnded(GameState state)
		{
			Player winner = state.getCountries().get(0).getPlayer();
			scores.put(winner, scores.get(winner) + 1);
		}

		public Double call()
		{
			resetScores();

			writeHeaders();	

			int games = config.getInt("games", 2000);
			for (int i = 1; i <= games; ++i)
			{
				// Generate a random map
				GameState state = generator.generate(4, 2.5);
				
				game = new Game(players, state);
				game.addEventListener(this);

				game.run();

				if (i % 10 == 0)
					writeScores(i);
			}

			return (double) scores.get(tdPlayer) / games;
		}

		private void resetScores()
		{
			scores = new HashMap<Player, Integer>();
			for (Player player : players)
				scores.put(player, 0);
		}

		private void writeHeaders()
		{
			if (writer == null)
				return;

			writer.write("Round");
			writer.write(players);
			writer.write("error");
			writer.write("variance");
			writer.endLine();
		}

		private void writeScores(int i)
		{
			if (writer == null)
				return;

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
		if (args.length < 1)
		{
			usage();
			return;
		}

		// Read default configuration
		File defaultConfigFile = new File(args[0]);
		Configuration defaultConfig = new Configuration();
		defaultConfig.read(defaultConfigFile);

		ExecutorService queue = Executors.newFixedThreadPool(8);
		HashMap<String, Vector<Future<Double>>> results = new HashMap<String, Vector<Future<Double>>>();

		// For each other configuration file provided, create a few experiments
		if (args.length > 1)
		{
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
					Future<Double> task = queue.submit(experiment);
					
					if (!results.containsKey(configFile.getName()))
						results.put(configFile.getName(), new Vector<Future<Double>>());
					
					results.get(configFile.getName()).add(task);
				}
			}
		}
		// If no additional configuration is provided, run just one experiment and print the output to stdout.
		else
		{
			Experiment experiment = new Experiment(defaultConfig, null);

			Future<Double> task = queue.submit(experiment);

			results.put(defaultConfigFile.getName(), new Vector<Future<Double>>());
			results.get(defaultConfigFile.getName()).add(task);
		}

		// No more new experiments will be added.
		queue.shutdown();
		
		// While the experiments are run, print the status of said experiments.
		while (queue.awaitTermination(1, TimeUnit.SECONDS) == false)
			printStatus(results);

		// Done? Print summary of the results!
		printResults(results);
	}

	static private void printStatus(HashMap<String, Vector<Future<Double>>> results)
	{
		int countedTasks = 0;
		int finishedTasks = 0;
		for (String configFileName : results.keySet())
			for (Future<Double> task : results.get(configFileName))
			{
				countedTasks++;

				if (task.isDone())
					finishedTasks++;
			}

		System.out.println("Finished " + finishedTasks + " of " + countedTasks + " tasks");
	}

	static private void printResults(HashMap<String, Vector<Future<Double>>> results) throws Exception
	{
		for (String configFileName : results.keySet())
		{
			Vector<Future<Double>> scores = results.get(configFileName);

			Instrument m = new Instrument(scores.size());
			for (Future<Double> score : scores)
				m.add(score.get());

			System.out.println(configFileName + ":"
				+ "\tavg:" + m.mean()
				+ "\tvar:" + m.variance());
		}
	}

	static public void usage()
	{
		System.out.println("Usage: TestGame default.conf test1.conf test2.conf ...");
	} 
}
