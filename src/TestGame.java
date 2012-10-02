import java.util.*;
import java.io.IOException;

class TestGame
{
	static public void main(String[] args)
	{
		List<Player> players = new Vector<Player>();
		players.add(new RandomPlayer("a"));
		players.add(new RandomPlayer("b"));
		players.add(new RandomPlayer("c"));
		players.add(new RandomPlayer("d"));
		players.add(new TerminalPlayer("e"));

		MapReader reader = new MapReader(players);

		try {
			List<Country> countries = reader.read(args[0]);
		
			GameState state = new GameState(countries);

			Game game = new Game(players, state);

			for (Player player : players)
				game.distributeNewDice(player, 10);

			do {
				game.step();

				System.out.print(game.state);

				if (game.livingPlayers().size() == 1)
				{
					System.out.println("We have a winner!");
					break;
				}
			}
			while (confirmContinue());
		}
		catch (IOException e) {
			System.out.println("Could not read map: " + e.getMessage());
			return;
		}
	}

	static private boolean confirmContinue()
	{
		try {
			java.io.BufferedReader stdin = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
			System.out.println("Press enter to continue...");
			String line = stdin.readLine();
			return true;
		}
		catch (java.io.IOException e)
		{
			return false;
		}
	}
}