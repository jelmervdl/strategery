import csv.CSVWriter;

import descriptors.Dominance;

import java.util.*;
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

		File defaultConfigFile = new File(args[0]);
		Configuration defaultConfig = new Configuration();
		defaultConfig.read(defaultConfigFile);

		for (int i = 1; i < args.length; ++i)
		{
			File configFile = new File(args[i]);

			if (!configFile.exists())
				continue;

			Configuration config = new Configuration(defaultConfig);
			config.read(configFile);

			File outputFile = new File(args[i] + ".csv");
			CSVWriter output = new CSVWriter(new PrintStream(outputFile));

			Experiment experiment = new Experiment(config, output);
			new Thread(experiment).start();
		}	
	}

	static public void usage()
	{
		System.out.println("Usage: TestGame default.conf test1.conf test2.conf ...");
	} 
}
