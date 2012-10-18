import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;

import game.GameState;
import game.Player;
import game.RandomPlayer;
import map.MapGenerator;
import ui.graphical.MapPanel;

public class TestGUI
{
	public static void main(String[] args)
	{
		JFrame frame = new JFrame();
		frame.setTitle("Random map");
		frame.setSize(500, 500);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		// Generate some players
		List<Player> players = new Vector<Player>();
		for (int i = 0; i < 5; ++i)
			players.add(new RandomPlayer("Player " + i));

		// Generate a map
		MapGenerator generator = new MapGenerator(players);
		GameState state = generator.generate(16, 3.5);

		// Add the map panel
		MapPanel mapPanel = new MapPanel();
		mapPanel.setState(state);

		// .. to the window
		Container contentPane = frame.getContentPane();
		contentPane.add(mapPanel);

		// and show it.
		frame.setVisible(true);
	}
}
