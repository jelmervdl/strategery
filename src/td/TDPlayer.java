package td;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import descriptors.Descriptor;
import descriptors.Dominance;

import game.GameState;
import game.Move;
import game.PlayerAdapter;

import util.Configuration;

public class TDPlayer extends PlayerAdapter
{
    private GameState previousState;

    private TDLearning td;

    private Configuration config;

    public TDPlayer(String name, TDLearning brain, Configuration configuration)
    {
		super(name);
        td = brain;
        config = configuration;
	}

    public void setConfiguration(Configuration config)
    {
        this.config = config;
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
        // If the chosen move results in us winning the game, learn so.
        if (result.isWonBy(this))
            evaluatePreviousMove(state);
        // Otherwise, wait till the other players have played, and then
        // learn the value of the game state we chose.
        else
            previousState = result;
    }

    private boolean shoudWeExplore()
    {
        double explorationRate = config.getDouble("exploration_rate", 0.005);
        double decrease = config.getDouble("exploration_rate_decrease", 0.0);
        double chance = explorationRate - decrease * td.getUpdates();

        return new Random().nextDouble() < chance;
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
     * Stupid simple value learning.
     */
    private void evaluatePreviousMove(GameState outcome)
    {
        double value = outcome.isWonBy(this)
            ? config.getDouble("winning_reward", 1.0)
            : getValue(outcome) + config.getDouble("gamma", 0.8) * td.getExpectedValue(this, outcome);

        // Adjust the NN for the move it just did to the actual value of the outcome of that move
        td.adjustNetwork(this, previousState, value);
    }

    /**
     * Off-policy learning.
     */
    // private void evaluatePreviousMove(GameState outcome)
    // {
    //     double gamma = 0.25;

    //     // Find all the moves that could be made from this state
    //     List<Move> possibleMoves = outcome.generatePossibleMoves(this);

    //     Map<Move,Double> expectedValues = td.getExpectedValues(this, outcome, possibleMoves);
        
    //     // Calculate the maximum expected value of the state we then enter
    //     double expectedFutureValue = Collections.max(expectedValues.values());

    //     // Adjust the NN for the move it just did to the actual value of the outcome of that
    //     // move plus what good it will do in the future.
    //     td.adjustNetwork(this, previousState, getValue(outcome) + gamma * expectedFutureValue);
    // }

    /**
     * On-policy learning.
     */
    // private void evaluatePreviousMove(GameState outcome)
    // {
    //     double gamma = 0.25;

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
