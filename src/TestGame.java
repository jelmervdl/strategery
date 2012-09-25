import java.util.*;

class TestGame
{
	static public void main(String[] args)
	{
		Player a = new RandomPlayer("a");
		Player b = new RandomPlayer("b");
		Player c = new RandomPlayer("c");
		Player d = new TerminalPlayer("d");

		/*
		  c1
		c2  c3
		  c4
		*/

		Country c1 = new Country(a, 2);
		Country c2 = new Country(b, 2);
		Country c3 = new Country(c, 2);
		Country c4 = new Country(d, 2);

		c1.neighbours.add(c2);
		c1.neighbours.add(c3);

		c2.neighbours.add(c1);
		c2.neighbours.add(c4);

		c3.neighbours.add(c1);
		c3.neighbours.add(c4);

		c4.neighbours.add(c2);
		c4.neighbours.add(c3);


		Set<Player> players = new HashSet<Player>();
		players.add(a);
		players.add(b);
		players.add(c);
		players.add(d);

		Vector<Country> countries = new Vector<Country>();
		countries.add(c1);
		countries.add(c2);
		countries.add(c3);
		countries.add(c4);

		GameState state = new GameState(countries);

		Game game = new Game(players, state);

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