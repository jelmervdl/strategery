import java.util.List;
import java.util.Vector;
import game.GameState;
import game.Player;
import game.RandomPlayer;

public class TestMapGeneration
{
	static public void main(String[] args)
	{
		List<Player> players = new Vector<Player>();
		players.add(new RandomPlayer("a"));
		players.add(new RandomPlayer("b"));
		
		GameState gamestate = new GameState(4, players, 2);
	}
}