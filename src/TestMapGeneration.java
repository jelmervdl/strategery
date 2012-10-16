import java.util.List;
import java.util.Vector;
import game.GameState;
import game.Player;
import game.RandomPlayer;

import map.MapGenerator;

public class TestMapGeneration
{
	static public void main(String[] args)
	{
		List<Player> players = new Vector<Player>();
		players.add(new RandomPlayer("a"));
		players.add(new RandomPlayer("b"));

		MapGenerator generator = new MapGenerator(players);
		
		GameState gamestate = generator.generate(4, 2);
	}
}