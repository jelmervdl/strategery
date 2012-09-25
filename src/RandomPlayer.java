import java.util.Random;
import java.util.Vector;

class RandomPlayer extends Player
{
	public RandomPlayer(String name)
	{
		super(name);
	}

	public Move decide(Vector<Move> possibleMoves, GameState state)
	{
		Random random = new Random();

		// All possible moves plus the [end this turn]-move (the +1 which returns NULL)
		int move = random.nextInt(possibleMoves.size() + 1);

		return move == possibleMoves.size()
			? null
			: possibleMoves.get(move);
	}
}