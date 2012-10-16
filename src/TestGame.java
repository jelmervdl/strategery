import java.util.*;
import java.io.IOException;
import game.*;
import gui.terminal.TerminalGui;
import gui.terminal.TerminalPlayer;
import map.MapReader;

public class TestGame
{
	static public void main(String[] args)
	{
		List<Player> players = new Vector<Player>();
		players.add(new RandomPlayer("a"));
		players.add(new RandomPlayer("b"));
		players.add(new RandomPlayer("c"));
		players.add(new RandomPlayer("d"));
		players.add(new TDPlayer("e"));

		MapReader reader = new MapReader(players);

		TerminalGui gui = new TerminalGui();

		String path = args.length > 0
			? args[0]
			: "../maps/1.txt";

		try {
			List<Country> countries = reader.read(path);
		
			GameState state = new GameState(countries);

			Game game = new Game(players, state);
			game.addEventListener(gui);

			game.play();
		}
		catch (IOException e) {
			System.out.println("Could not read map: " + e.getMessage());
			return;
		}
	}
}
