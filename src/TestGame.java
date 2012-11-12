import csv.CSVWriter;

import descriptors.Dominance;

import java.util.*;
import java.io.File;
import java.io.IOException;

import game.*;

import map.MapGenerator;

import td.TDLearning;
import td.TDPlayer;

import ui.terminal.TerminalUI;
import ui.terminal.TerminalPlayer;

import util.Configuration;

public class TestGame 
{
	static public void main(String[] args) throws Exception
	{
		final Configuration config = new Configuration();

		if (args.length > 0 && new File(args[0]).exists())
			config.read(new File(args[0]));

		final TDLearning brain = new TDLearning(config.getSection("network"));

		final TDPlayer tdPlayer = new TDPlayer("TD", brain, config.getSection("player"));

		List<Player> players = new Vector<Player>();
		players.add(tdPlayer);
		// players.add(new TDPlayer("TD 2", brain));
		// players.add(new TDPlayer("TD 3", brain));
		// players.add(new TDPlayer("TD 4", brain));
		players.add(new RandomPlayer("Random"));
		players.add(new SimplePlayer("Simple"));
		// players.add(new DescriptorPlayer("Dominance", new Dominance()));

		TerminalUI gui = new TerminalUI();

		MapGenerator generator = new MapGenerator(players);

		// Initialize scores table
		final HashMap<Player, Integer> scores = new HashMap<Player, Integer>();
		for (Player player : players)
			scores.put(player, 0);

		CSVWriter writer = new CSVWriter(System.out);
		writer.write("Round");
		writer.write(players);
		writer.write("error");
		writer.write("variance");
		writer.endLine();

		int epochs = config.getInt("epochs", 2000);
		for (int i = 1; i <= epochs; ++i)
		{
			// Generate a random map
			GameState state = generator.generate(4, 2.5);
			
			final Game game = new Game(players, state);
			game.addEventListener(new GameEventAdapter() {
				public void onTurnEnded(GameState state)
				{
					if (!state.getPlayers().contains(tdPlayer))
					{
						System.out.println("Stopping game because TDPlayer died");
						game.stop();
					}
				}
				
				public void onGameEnded(GameState state)
				{
					Player winner = state.getCountries().get(0).getPlayer();
					scores.put(winner, scores.get(winner) + 1);

					// System.out.println("Winner: " + winner);
				}
			});
			// game.addEventListener(gui);
			game.run();

			if (i % 10 == 0)
			{
				writer.write(i);
				
				for (Player player : players)
					writer.write(scores.get(player));

				writer.write(brain.getError().mean());
				writer.write(brain.getError().variance());
				writer.endLine();
			}
		}

		// Print total scores table
		System.out.println();
		System.out.println("# Total scores:");
		for (Player player : players)
			System.out.println("# Player " + player + ": \t" + scores.get(player));
	}
}
