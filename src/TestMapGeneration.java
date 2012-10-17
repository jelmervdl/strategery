import java.util.List;
import java.util.Vector;
import game.GameState;
import game.Player;
import game.RandomPlayer;
import game.Country;

import map.MapGenerator;

public class TestMapGeneration
{
	static public void main(String[] args)
	{
		Vector<Player> players = new Vector<Player>();
		players.add(new RandomPlayer("a"));
		players.add(new RandomPlayer("b"));
		players.add(new RandomPlayer("c"));
		players.add(new RandomPlayer("d"));

		MapGenerator generator = new MapGenerator(players);
		
		GameState gamestate = generator.generate(16, 3.5);
	}
}