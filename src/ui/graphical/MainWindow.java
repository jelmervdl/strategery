package ui.graphical;

import java.awt.BorderLayout;
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
import game.PlayerAdapter;
import game.RandomPlayer;
import game.GameEventListener;

import td.GameStateEncoder;

public class MainWindow extends JFrame implements GameEventListener
{
	private Game game;

	private Thread gameThread;

	private MapPanel mapPanel;

	private DescriptorPanel descriptorPanel;

	private JFrame descriptorWindow;

	private Player player;

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
		getContentPane().add(mapPanel, BorderLayout.CENTER);

		descriptorPanel = new DescriptorPanel(GameStateEncoder.buildDefaultEncoder().getDescriptors());
		
		descriptorWindow = new JFrame();
		descriptorWindow.getContentPane().add(descriptorPanel);
		descriptorWindow.setVisible(true);
	}

	public void setGame(Game game)
	{
		if (this.game != null)
		{
			this.game.removeEventListener(this);
			this.game.stop();
			this.gameThread.interrupt();
		}

		this.game = game;
		setState(this.game.getState());
		
		this.game.addEventListener(this);

		this.gameThread = new Thread(this.game);
	}

	public void setState(GameState state)
	{
		mapPanel.setState(state);
		descriptorPanel.update(state, player);
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

	public Player getPlayer()
	{
		if (player == null)
		{
			player = new PlayerAdapter("GUI Player")
			{
				public Color getColor()
				{
					return Color.RED;
				}

				public Move decide(List<Move> possibleMoves, GameState state)
				{
					if (possibleMoves.size() == 1)
						return possibleMoves.get(0);

					// Get attacking country
					HashSet<Country> attackingCountries = new HashSet<Country>();
					for (Move move : possibleMoves)
						if (!move.isEndOfTurn())
							attackingCountries.add(move.getAttackingCountry());

					Country attackingCountry = CountrySelector.run(gameThread, mapPanel, attackingCountries);

					// Get defending country (or attacked country)
					HashSet<Country> defendingCountries = new HashSet<Country>();
					for (Move move : possibleMoves)
						if (!move.isEndOfTurn() && move.getAttackingCountry().equals(attackingCountry))
							defendingCountries.add(move.getDefendingCountry());

					Country defendingCountry = CountrySelector.run(gameThread, mapPanel, defendingCountries);
					
					// Return that move.
					for (Move move : possibleMoves)
						if (!move.isEndOfTurn()
							&& move.getAttackingCountry().equals(attackingCountry)
							&& move.getDefendingCountry().equals(defendingCountry))
							return move;

					throw new RuntimeException("Could not find move in list of possible moves");
				}
			};
		}

		return player;
	}
}
