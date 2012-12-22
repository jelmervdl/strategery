import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.Vector;

import game.Game;
import game.GameEventAdapter;
import game.GameState;
import game.Player;
import td.TDLearning;
import td.TDPlayer;
import map.MapGenerator;
import ui.graphical.MainWindow;
import ui.graphical.MapPanel;
import util.Configuration;

public class TestGUI
{
	static private class Controller extends GameEventAdapter
	{
		Configuration config;

		TDLearning brain;

		MainWindow mainWindow;

		File memoryFile;

		public Controller() throws Exception
		{
			// Set up configuration
			config = new Configuration();

			File configFile = new File("./default.conf");
			if (configFile.exists())
				config.read(configFile);

			// Set up learning player's brain
			brain = new TDLearning(config.getSection("network"));

			// See if we can resume learning
			memoryFile = new File("./memory.dat");
			if (memoryFile.exists())
				brain.getNeuralNetwork().readWeights(memoryFile);

			// Set up the main window
			mainWindow = new MainWindow();
		}

		public void onGameEnded(GameState state)
		{
			try {
				brain.getNeuralNetwork().writeWeights(memoryFile);
			} catch (IOException e) {
				System.err.println("Could not brainstate: " + e);
			}

			reset();
		}

		public void reset()
		{
			// Line up some players
			List<Player> players = new Vector<Player>();
			players.add(mainWindow.getPlayer());
			players.add(new TDPlayer("Enemy 1", brain, config.getSection("player")));
			players.add(new TDPlayer("Enemy 2", brain, config.getSection("player")));

			// Generate a map
			MapGenerator generator = new MapGenerator(players);
			GameState state = generator.generate(4, 3.5);

			Game game = new Game(players, state);
			game.addEventListener(this);
			
			mainWindow.setGame(game);
			mainWindow.setVisible(true);
			mainWindow.startGame();
		}

		public void run()
		{
			// Set up game
			reset();
		}
	}

	public static void main(String[] args) throws Exception
	{
		new Controller().run();
	}
}
