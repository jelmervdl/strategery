import java.util.Vector;

class TestGame
{
	static public void main(String[] args)
	{
		Player a = new Player("a");
		Player b = new Player("b");

		/*
		  c1
		c2  c3
		  c4
		*/

		Country c1 = new Country(a, 2);
		Country c2 = new Country(a, 2);
		Country c3 = new Country(b, 2);
		Country c4 = new Country(b, 2);

		c1.neighbours.add(c2);
		c1.neighbours.add(c3);

		c2.neighbours.add(c1);
		c2.neighbours.add(c4);

		c3.neighbours.add(c1);
		c3.neighbours.add(c4);

		c4.neighbours.add(c2);
		c4.neighbours.add(c3);


		Vector<Player> players = new Vector<Player>();
		players.add(a);
		players.add(b);

		Vector<Country> countries = new Vector<Country>();

		GameState state = new GameState(countries);

		Game game = new Game(players, state);

		game.step();
	}
}