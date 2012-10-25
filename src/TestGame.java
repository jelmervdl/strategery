import java.util.*;
import java.io.File;
import java.io.IOException;
import game.*;
import ui.terminal.TerminalUI;
import ui.terminal.TerminalPlayer;
import map.MapGenerator;
import td.TDPlayer;
import csv.CSVWriter;
import descriptors.Dominance;

public class TestGame 
{
	static public void main(String[] args)
	{
		final TDPlayer tdPlayer = new TDPlayer("TD");

		List<Player> players = new Vector<Player>();
		players.add(tdPlayer);
		players.add(new RandomPlayer("Random"));
		players.add(new SimplePlayer("Simple"));
		players.add(new DescriptorPlayer("Dominance", new Dominance()));

		TerminalUI gui = new TerminalUI();

		MapGenerator generator = new MapGenerator(players);

		// Initialize scores table
		final HashMap<Player, Integer> scores = new HashMap<Player, Integer>();
		for (Player player : players)
			scores.put(player, 0);

		CSVWriter writer = new CSVWriter(System.out);
		writer.writeln(players);

		for (int i = 0; i < 10000; ++i)
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

			if (i % 100 == 0)
			{
				List<Integer> currentScores = new Vector<Integer>(players.size());
				for (Player player : players)
					currentScores.add(scores.get(player));

				writer.writeln(currentScores);
			}
		}

		// Print total scores table
		System.out.println("Total scores:");
		for (Player player : players)
			System.out.println("Player " + player + ": \t" + scores.get(player));
	}
}
