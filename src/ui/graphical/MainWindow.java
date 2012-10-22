package ui.graphical;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;

import game.Country;
import game.Game;
import game.GameState;
import game.Move;
import game.Player;
import game.RandomPlayer;
import game.GameEventListener;

import ui.graphical.MapPanel;

public class MainWindow extends JFrame implements GameEventListener
{
	private Game game;

	private Thread gameThread;

	private MapPanel mapPanel;

	public MainWindow(Game game)
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

		this.game = game;
		setState(game.getState());
		game.addEventListener(this);

		gameThread = new Thread(game);
	}

	public void setState(GameState state)
	{
		mapPanel.setState(state);
	}

	public void startGame()
	{
		gameThread.start();
	}

	public void pauseGame(int timeout)
	{
		try {
			gameThread.sleep(timeout);
		}
		catch (InterruptedException e) {
			System.out.println("InterruptedException: " + e);
		}
	}

	/* Implementing GameEventListener interface */

	public void onStep()
	{
		pauseGame(500);
	}

	public void onChooseMove(List<Move> moves)
	{
		HashSet<Country> highlights = new HashSet<Country>();
		
		for (Move move : moves)
		{
			if (move.isEndOfTurn())
				continue;
			
			highlights.add(move.getAttackingCountry());
			highlights.add(move.getDefendingCountry());
		}

		mapPanel.setHighlights(highlights);
		pauseGame(200);
	}

	public void onMove(Move move)
	{
		HashSet<Country> highlights = new HashSet<Country>();

		if (move != null && !move.isEndOfTurn())
		{
			highlights.add(move.getAttackingCountry());
			highlights.add(move.getDefendingCountry());
		}

		mapPanel.setHighlights(highlights);
		pauseGame(500);
	}

	public void onStateChange(GameState state)
	{
		// mapPanel.setHighlights(null);
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
