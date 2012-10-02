import java.util.List;

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

	abstract public Move decide(List<Move> possibleMoves, GameState state);
}