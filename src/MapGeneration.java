import java.util.*;
import java.io.IOException;

class MapGeneration
{
	static public void main(String[] args)
	{
		List<Player> players = new Vector<Player>();
		players.add(new RandomPlayer("a"));
		players.add(new RandomPlayer("b"));
		
		GameState gamestate = new GameState(4, players, 2);
	}
}