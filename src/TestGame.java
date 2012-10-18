import java.util.*;
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
		List<Player> players = new Vector<Player>();
		players.add(new RandomPlayer("a"));
		players.add(new RandomPlayer("b"));
		players.add(new RandomPlayer("c"));
		players.add(new RandomPlayer("d"));
		players.add(new TDPlayer("e"));

		MapReader reader = new MapReader(players);

		TerminalUI gui = new TerminalUI();

		String path = args.length > 0
			? args[0]
			: "../maps/1.txt";

		try {
			List<Country> countries = reader.read(path);
		
			GameState state = new GameState(countries);

			Game game = new Game(players, state);
			game.addEventListener(gui);

			game.run();
		}
		catch (IOException e) {
			System.out.println("Could not read map: " + e.getMessage());
			return;
		}
	}
}
