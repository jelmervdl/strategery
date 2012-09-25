import java.util.Vector;

class BoringPlayer extends Player
{
	public BoringPlayer(String name)
	{
		super(name);
	}

	public Move decide(Vector<Move> possibleMoves, GameState state)
	{
		return null;
	}
}