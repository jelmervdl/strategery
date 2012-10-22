package ui.terminal;

import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import game.*;

public class TerminalPlayer extends PlayerAdapter
{
	BufferedReader stdin;

	public TerminalPlayer(String name)
	{
		super(name);

		stdin = new BufferedReader(new InputStreamReader(System.in));
	}

	public Move decide(List<Move> possibleMoves, GameState state)
	{
		System.out.println("Your turn! Which move will you play?");

		for (int i = 0; i < possibleMoves.size(); ++i)
			System.out.println(i + ". " + possibleMoves.get(i));

		System.out.println("0. End of turn");
		int moveIndex;

		try {
			do {
				System.out.print("Your move: ");

				String line = stdin.readLine();
				moveIndex = Integer.parseInt(line);
			} while (moveIndex < 0 || moveIndex > possibleMoves.size());
		}
		catch (IOException e)
		{
			moveIndex = 0;
		}

		return possibleMoves.get(moveIndex - 1);
	}
}