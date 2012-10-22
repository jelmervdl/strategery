package game;

import java.util.List;

public class SimplePlayer extends PlayerAdapter
{
	public SimplePlayer(String name)
	{
		super(name);
	}

	public Move decide(List<Move> possibleMoves, GameState state)
	{
		Move bestMove = null;
		int largestDiceDifference = -1; // so 7 vs 7 will still result in moves.

		for (Move move : possibleMoves)
		{
			int diff = move.getAttackingCountry().dice - move.getDefendingCountry().dice;
			if (diff > largestDiceDifference)
			{
				bestMove = move;
				largestDiceDifference = diff;
			}
		}

		return bestMove;
	}
}
