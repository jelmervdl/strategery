package ui.terminal;

import java.util.*;
import game.*;

public class TerminalUI implements GameEventListener
{
	private int turns;

	private Player currentPlayer;

	private GameState currentState;

	public TerminalUI()
	{
		turns = 0;
	}

	public void onStep()
	{
		// This will block on commandline input.
		if (!confirm())
			System.exit(0);

		// Count the number of turns of the game.
		++turns;
	}

	public void onMove(Move move)
	{
		System.out.println("Player " + currentPlayer + " will play move " + move);
	}

	public void onStateChange(GameState state)
	{
		currentState = state;
	}

	public void onTurnStarted(Player player)
	{
		System.out.println("Turn started for " + player);
		currentPlayer = player;
	}

	public void onTurnEnded(Player player)
	{
		assert player == currentPlayer : "The player that ended the turn is not the same as that started it.";

		System.out.println("Turn ended for " + player);
		currentPlayer = null;
	}

	public void onGameStarted(GameState state)
	{
		System.out.println("The game has started");
		currentState = state;
	}

	public void onGameEnded(GameState state)
	{
		System.out.println("The game has ended");
		currentState = state;
	}

	private boolean confirm()
	{
		try {
			java.io.BufferedReader stdin = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
			System.out.println("Press enter to continue...");
			String line = stdin.readLine();
			return true;
		}
		catch (java.io.IOException e)
		{
			return false;
		}
	}
}