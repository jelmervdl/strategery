package td;

import java.util.*;

import game.GameState;
import game.Move;
import game.PlayerAdapter;
import descriptors.Descriptor;
import descriptors.Dominance;

public class TDPlayer extends PlayerAdapter
{
    private TDLearning td;
    
    private GameState previousState;

	public TDPlayer(String name, TDLearning brain)
    {
		super(name);
        td = brain != null ? brain : new TDLearning();
	}

	public Move decide(List<Move> possibleMoves, GameState state)
	{
        // If we remember our previous state, and this state is not directly after
        // the previous state, learn from it.
        if (previousState != null && !previousState.equals(state))
            evaluatePreviousMove(state);

        // Calculate the value of the states resulting from the possible moves.        
        HashMap<Move, Double> possibleStates = td.mapValueToMove(this, state, possibleMoves);
        
        // From the list of values mapped to the moves select the best move according to policy
        Move move = selectAction(possibleStates);
        
        // return the move that is to be executed
        return move;
    }

    public void feedback(GameState state, Move move, GameState result)
    {
        // should we train the network here? Now we know the input
        // state (same as given to decide()), the move chosen, and
        // the effect it had directly after doing the move.

        // Otherwise, we could store the move chosen, and when decide
        // is called again, use the 'new' state as resulting state which
        // is the state after all other players have made their move.

        // I don't know if this is the right place for this. We might want to
        // do this to the most likely outcome instead of the real outcome
        // of a move. But I'm doing it anyway. Just because.
        previousState = result;
    }

    public Move selectAction(HashMap<Move, Double> possibleStates)
    {
        Random random = new Random();
        
        // Exploration strategy is to choose a random move. (yes, this is crude)
        if (random.nextDouble() < 0.1)
        {
            int index = random.nextInt(possibleStates.size());
            for (Move move : possibleStates.keySet())
                if (index-- == 0)
                    return move;
        }
        
        // apply policy
        Move move = null;
        double max = Double.NEGATIVE_INFINITY;

        for (Map.Entry<Move, Double> pair : possibleStates.entrySet())
        {
            if (pair.getValue() > max)
            {
                max = pair.getValue();
                move = pair.getKey();
            }
        }

        return move;
    }

    private void evaluatePreviousMove(GameState outcome)
    {
        // Adjust the NN for the move it just did.
        td.adjustNetwork(this, previousState, calculateReward(outcome) > calculateReward(previousState) ? 1 : -1);
    }

    private double calculateReward(GameState state)
    {
        // Missuse the Dominance descriptor as reward function.
        Descriptor rewardFunction = new Dominance();

        return rewardFunction.describe(state, this);
    }
}
