import java.util.Random;
import java.util.Vector;

class Player
{
	String name;

	public Player(String name)
	{
		this.name = name;
	}

	public Move decide(Vector<Move> possibleMoves, GameState state)
	{
		Random random = new Random();

		int move = random.nextInt(possibleMoves.size() + 1);

		return move == possibleMoves.size()
			? null
			: possibleMoves.get(move);
	}

	public String toString()
	{
		return "Player " + name;
	}
}