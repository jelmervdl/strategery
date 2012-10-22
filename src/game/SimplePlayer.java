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
		Move bestMove = possibleMoves.get(0); // The end-of-turn move
		int largestDiceDifference = -1; // so 7 vs 7 will still result in moves.

		for (Move move : possibleMoves)
		{
			if (move.isEndOfTurn())
				continue;
			
			int diff = move.getAttackingCountry().getDice() - move.getDefendingCountry().getDice();
			if (diff > largestDiceDifference)
			{
				bestMove = move;
				largestDiceDifference = diff;
			}
		}

		return bestMove;
	}
}
