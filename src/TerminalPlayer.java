import java.util.Vector;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

class TerminalPlayer extends Player
{
	BufferedReader stdin;

	public TerminalPlayer(String name)
	{
		super(name);

		stdin = new BufferedReader(new InputStreamReader(System.in));
	}

	public Move decide(Vector<Move> possibleMoves, GameState state)
	{
		System.out.println("Your turn! Which move will you play?");

		for (int i = 0; i < possibleMoves.size(); ++i)
			System.out.println((i + 1) + ". " + possibleMoves.elementAt(i));

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

		return moveIndex == 0
			? null
			: possibleMoves.elementAt(moveIndex - 1);
	}
}