package game;

import java.util.List;
import descriptors.Descriptor;
import td.Chance;

public class DescriptorPlayer extends PlayerAdapter
{
	private Descriptor descriptor;

	public DescriptorPlayer(String name, Descriptor descriptor)
	{
		super(name);
		this.descriptor = descriptor;
	}

	public Move decide(List<Move> possibleMoves, GameState state)
	{
		Move bestMove = null;
		double bestValue = Double.NEGATIVE_INFINITY;

		for (Move move : possibleMoves)
		{
			double expectedValue = 0;

			if (move.isEndOfTurn())
			{
				expectedValue = descriptor.describe(state, this);
			}
			else
			{
				int attackingEyes = move.getAttackingCountry().getDice();
				int defendingEyes = move.getDefendingCountry().getDice();

				//win
				expectedValue = Chance.chanceTable(attackingEyes, defendingEyes) * descriptor.describe(state.expectedState(move, 1), this);
            
            	//draw
				double drawChance = 1 - Chance.chanceTable(attackingEyes, defendingEyes) - Chance.chanceTable(defendingEyes, attackingEyes);
				expectedValue += drawChance * descriptor.describe(state.expectedState(move, 2), this);
	            
				//lose        
				expectedValue += Chance.chanceTable(defendingEyes, attackingEyes) * descriptor.describe(state.expectedState(move, 3), this);
			}

			if (expectedValue > bestValue)
			{
				bestMove = move;
				bestValue = expectedValue;
			}
		}

		return bestMove;
	}
}
