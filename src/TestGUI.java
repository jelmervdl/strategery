import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;

import game.Game;
import game.GameState;
import game.Player;
import game.RandomPlayer;
import game.SimplePlayer;
import map.MapGenerator;
import ui.graphical.MainWindow;
import ui.graphical.MapPanel;

public class TestGUI
{
	public static void main(String[] args)
	{
		MainWindow mainWindow = new MainWindow();

		// Generate some players
		List<Player> players = new Vector<Player>();
		players.add(mainWindow.getPlayer());
		for (int i = 0; i < 3; ++i)
			players.add(new RandomPlayer("Player " + i));

		// Generate a map
		MapGenerator generator = new MapGenerator(players);
		GameState state = generator.generate(4, 3.5);

		Game game = new Game(players, state);
		
		mainWindow.setGame(game);
		mainWindow.setVisible(true);

		mainWindow.startGame();
	}
}
