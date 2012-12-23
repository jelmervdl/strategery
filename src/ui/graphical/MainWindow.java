package ui.graphical;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

	private Button endTurnButton;

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

		endTurnButton = new Button("End turn");
		getContentPane().add(endTurnButton, BorderLayout.SOUTH);
		endTurnButton.setVisible(false);

		getContentPane().validate();
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
		/*
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
		*/
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
		System.out.println("The Game has Ended");
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
					// If the only move is end-of-turn, return that one automatically
					if (possibleMoves.size() == 1)
						return possibleMoves.get(0);

					// Set up the country selectors here so we know of them (and can cancel them)
					final CountrySelector countrySelector = new CountrySelector(mapPanel);
					
					// Find end-of-turn move in case we need to return it.
					Move endOfTurn = null;
					for (Move move : possibleMoves)
					{
						if (move.isEndOfTurn())
						{
							endOfTurn = move;
							break;
						}
					}

					// Show the end-of-turn button. It only cancels the selector. If a selector
					// returns while it has no selection, this probably happened.
					endTurnButton.setVisible(true);
					endTurnButton.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							countrySelector.cancel();
						}
					});
					getContentPane().validate();

					// Get attacking country
					HashSet<Country> attackingCountries = new HashSet<Country>();
					for (Move move : possibleMoves)
						if (!move.isEndOfTurn())
							attackingCountries.add(move.getAttackingCountry());

					countrySelector.setOptions(attackingCountries);
					countrySelector.run(gameThread);

					if (countrySelector.isCancelled())
					{
						endTurnButton.setVisible(false);
						getContentPane().validate();
						return endOfTurn;
					}

					Country attackingCountry = countrySelector.getSelection();

					HashSet<Country> defendingCountries = new HashSet<Country>();
					for (Move move : possibleMoves)
						if (!move.isEndOfTurn() && move.getAttackingCountry().equals(attackingCountry))
							defendingCountries.add(move.getDefendingCountry());

					countrySelector.setOptions(defendingCountries);
					countrySelector.run(gameThread);

					// Apparently the country selector was cancelled, which can only be for once reason
					if (countrySelector.isCancelled())
					{
						endTurnButton.setVisible(false);
						getContentPane().validate();
						return endOfTurn;
					}
					
					Country defendingCountry = countrySelector.getSelection();

					endTurnButton.setVisible(false);
					getContentPane().validate();

					// Return the move.
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
