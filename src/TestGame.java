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

		HashMap<Player, Integer> scores;

		Game game;

		boolean testing;

		public Experiment(Configuration config, CSVWriter writer)
		{
			this.config = config;

			this.writer = writer;

			brain = new TDLearning(config.getSection("network"));

			tdPlayer = new TDPlayer("TD", brain, config.getSection("player"));

			testing = false;
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
			if (!testing)
				return;

			Set<Player> surviving = state.getPlayers();

			if (surviving.size() != 1)
				return;

			Player winner = surviving.iterator().next();
			scores.put(winner, scores.get(winner) + 1);
		}

		private void train(List<Player> players, int games)
		{
			MapGenerator generator = new MapGenerator(players);

			for (int i = 1; i <= games; ++i)
			{
				// Generate a random map
				GameState state = generator.generate(4, 2.5);
				
				game = new Game(players, state);
				game.addEventListener(this);

				game.run();
			}
		}

		private void test(List<Player> players, int games)
		{
			testing = true;

			MapGenerator generator = new MapGenerator(players);

			for (int i = 1; i <= games; ++i)
			{
				// Generate a random map
				GameState state = generator.generate(4, 2.5);
				
				game = new Game(players, state);
				game.addEventListener(this);

				game.run();

				writeScores(players, i);
			}

			testing = false;
		}

		private void trainAndTest(List<Player> trainPlayers, List<Player> testPlayers, int trainGames, int testGames)
		{
			MapGenerator generator = new MapGenerator(trainPlayers);

			for (int i = 0; i <= trainGames; ++i)
			{
				if (i % 10 == 0)
					test(testPlayers, testGames);
				else
					train(trainPlayers, 1);
			}
		}

		public Double call() throws Exception
		{
			List<Player> trainPlayers = new Vector<Player>();
			trainPlayers.add(tdPlayer);
			trainPlayers.add(new TDPlayer("TD2", brain, config.getSection("player")));
			
			List<Player> testPlayers = new Vector<Player>();
			testPlayers.add(tdPlayer);
			
			String testOpponentClassName = config.getString("opponent_class", "RandomPlayer");
			Class<? extends Player> testOpponentClass = Class.forName("game." + testOpponentClassName).asSubclass(Player.class);
			testPlayers.add(testOpponentClass.getConstructor(String.class).newInstance(testOpponentClassName));

			int games = config.getInt("games", 2000);
			
			// train(trainPlayers, games);

			resetScores(testPlayers);

			writeHeaders(testPlayers);
			
			// test(testPlayers, games);

			trainAndTest(trainPlayers, testPlayers, games, 10);

			return (double) scores.get(tdPlayer) / games;
		}

		private void resetScores(List<Player> players)
		{
			scores = new HashMap<Player, Integer>();
			for (Player player : players)
				scores.put(player, 0);
		}

		private void writeHeaders(List<Player> players)
		{
			if (writer == null)
				return;

			writer.write("Round");
			writer.write(players);
			writer.write("error");
			writer.write("variance");
			writer.endLine();
		}

		private void writeScores(List<Player> players, int i)
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

		System.err.println("Finished " + finishedTasks + " of " + countedTasks + " tasks");
	}

	static private void printResults(HashMap<String, Vector<Future<Double>>> results) throws Exception
	{
		for (String configFileName : results.keySet())
		{
			Vector<Future<Double>> scores = results.get(configFileName);

			Instrument m = new Instrument(scores.size());
			for (Future<Double> score : scores)
				m.add(score.get());

			System.err.println(configFileName + ":"
				+ "\tn:" + m.count()
				+ "\tavg:" + m.mean()
				+ "\tvar:" + m.variance());
		}
	}

	static public void usage()
	{
		System.out.println("Usage: TestGame default.conf test1.conf test2.conf ...");
	} 
}
