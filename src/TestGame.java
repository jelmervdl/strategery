import java.util.*;
import java.io.File;
import java.io.IOException;
import game.*;
import ui.terminal.TerminalUI;
import ui.terminal.TerminalPlayer;
import map.MapReader;
import td.TDPlayer;

public class TestGame 
{
	static public void main(String[] args)
	{
		final TDPlayer tdPlayer = new TDPlayer("td");

		List<Player> players = new Vector<Player>();
		players.add(tdPlayer);
		players.add(new SimplePlayer("Simple a"));
		players.add(new SimplePlayer("Simple b"));
		players.add(new SimplePlayer("Simple c"));
		players.add(new RandomPlayer("Random d"));

		MapReader reader = new MapReader(players);

		TerminalUI gui = new TerminalUI();

		String path = args.length > 0
			? args[0]
			: "../maps/1.txt";

		// Initialize scores table
		final HashMap<Player, Integer> scores = new HashMap<Player, Integer>();
		for (Player player : players)
			scores.put(player, 0);

		try {
			List<Country> countries = reader.read(new File(path));
		
			GameState state = new GameState(countries);

			for (int i = 0; i < 500; ++i)
			{
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
			}

			// Print total scores table
			System.out.println("Total scores:");
			for (Player player : players)
				System.out.println("Player " + player + ": \t" + scores.get(player));
		}
		catch (IOException e) {
			System.out.println("Could not read map: " + e.getMessage());
			return;
		}
	}
}
