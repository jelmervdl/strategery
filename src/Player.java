import java.util.Vector;

abstract class Player
{
	String name;

	public Player(String name)
	{
		this.name = name;
	}
	
	public String toString()
	{
		return "[Player " + name + "]";
	}

	abstract public Move decide(Vector<Move> possibleMoves, GameState state);
}