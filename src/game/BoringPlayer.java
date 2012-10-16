package game;

import java.util.List;

public class BoringPlayer extends Player
{
	public BoringPlayer(String name)
	{
		super(name);
	}

	public Move decide(List<Move> possibleMoves, GameState state)
	{
		return null;
	}
}