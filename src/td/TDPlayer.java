package td;

import java.util.List;
import java.util.Map;
import java.util.Random;

import descriptors.Descriptor;
import descriptors.Dominance;

import game.GameState;
import game.Move;
import game.PlayerAdapter;

public class TDPlayer extends PlayerAdapter
{
    private GameState previousState;

    private TDLearning td;

    public TDPlayer(String name, TDLearning brain)
    {
		super(name);
        td = brain;
	}

	public Move decide(List<Move> possibleMoves, GameState state)
	{
        // If we remember our previous state, and this state is not directly after
        // the previous state, learn from it.
        if (previousState != null && !previousState.equals(state))
            evaluatePreviousMove(state);

        // From the list of values mapped to the moves select the best move according to policy
        Move move = shoudWeExplore()
            ? exploreMove(state, possibleMoves)
            : chooseMove(state, possibleMoves);

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

    private boolean shoudWeExplore()
    {
        return new Random().nextDouble() < 0.1;
    }

    private Move exploreMove(GameState state, List<Move> moves)
    {
        int index = new Random().nextInt(moves.size());
        
        return moves.get(index);
    }

    private Move chooseMove(GameState state, List<Move> moves)
    {
        Move move = null;
        double max = Double.NEGATIVE_INFINITY;

        for (Map.Entry<Move, Double> pair : td.getExpectedValues(this, state, moves).entrySet())
        {
            if (pair.getValue() > max)
            {
                max = pair.getValue();
                move = pair.getKey();
            }
        }

        return move;
    }

    /**
     * Calculate the actual value of the state for me.
     */
    private double getValue(GameState state)
    {
        // Missuse the Dominance descriptor as reward function.
        return new Dominance().describe(state, this);
    }

    /**
     * Off-policy learning.
     */
    private void evaluatePreviousMove(GameState outcome)
    {
        double gamma = 0.5;

        // Find all the moves that could be made from this state
        List<Move> possibleMoves = outcome.generatePossibleMoves(this);

        Map<Move,Double> expectedValues = td.getExpectedValues(this, outcome, possibleMoves);
        
        // Calculate the average expected value of the state we then enter
        double expectedFutureValue = 0;

        for (double value : expectedValues.values())
            expectedFutureValue += value;

        expectedFutureValue /= expectedValues.size();

        // Adjust the NN for the move it just did to the actual value of the outcome of that
        // move plus what good it will do in the future.
        td.adjustNetwork(this, previousState, getValue(outcome) + gamma * expectedFutureValue);
    }

    /**
     * On-policy learning.
     */
    // private void evaluatePreviousMove(GameState outcome)
    // {
    //     double gamma = 0;

    //     // Find all the moves that could be made from this state
    //     List<Move> possibleMoves = outcome.generatePossibleMoves(this);

    //     // Choose the best possible move using our current policy
    //     Move bestPossibleMove = chooseMove(outcome, possibleMoves);

    //     // Calculate the expected value of the state we then enter
    //     double expectedFutureValue = td.getExpectedValue(this, outcome, bestPossibleMove);

    //     // Adjust the NN for the move it just did to the actual value of the outcome of that
    //     // move plus what good it will do in the future.
    //     td.adjustNetwork(this, previousState, getValue(outcome) + gamma * expectedFutureValue);
    // }
}
