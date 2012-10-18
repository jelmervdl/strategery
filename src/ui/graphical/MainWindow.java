package ui.graphical;

import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;

import game.GameState;
import game.Move;
import game.Player;
import game.RandomPlayer;
import game.GameEventListener;

import ui.graphical.MapPanel;

public class MainWindow extends JFrame implements GameEventListener
{
	private MapPanel mapPanel;

	public MainWindow()
	{
		super();

		setTitle("Strategery");
		setSize(500, 500);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		mapPanel = new MapPanel();
		getContentPane().add(mapPanel);
	}

	public void setState(GameState state)
	{
		mapPanel.setState(state);
	}

	public void onStep()
	{
		try {
			Thread.sleep(500);
		}
		catch (InterruptedException e) {
			System.out.println("InterruptedException: " + e);
		}
	}

	public void onMove(Move move)
	{

	}

	public void onStateChange(GameState state)
	{
		setState(state);
	}

	public void onTurnStarted(Player player)
	{

	}

	public void onTurnEnded(Player player)
	{

	}

	public void onGameStarted(GameState state)
	{

	}

	public void onGameEnded(GameState state)
	{

	}
}
