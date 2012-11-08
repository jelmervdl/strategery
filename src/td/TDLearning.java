package td;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import game.GameState;
import game.Move;
import game.Player;

import neuralnetwork.Layer;
import neuralnetwork.NeuralNetwork;

import util.Instrument;

public class TDLearning
{
    private GameStateEncoder encoder;

    private NeuralNetwork network;

    private Instrument error;

    public TDLearning()
    {
        // The encoder turns a GameState instance into a set of doubles.
        encoder = GameStateEncoder.buildDefaultEncoder();

        // Configuration of layers of the neural network
        int dimensions[] = new int[] {
            encoder.getDescriptors().size(), // input layer
            40, // hidden layer
            1 // output layer
        };

        network = new NeuralNetwork(dimensions);
        network.seed(0.1);

        error = new Instrument();
    }

    public Instrument getError()
    {
        return error;
    }

    public Map<Move, Double> getExpectedValues(Player player, GameState state, List<Move> moves)
    {
        // Initialize HashMap where the values get mapped to the possible moves
        HashMap<Move, Double> expectedValues = new HashMap<Move, Double>();        
        List<GameState> states = new Vector<GameState>();
       
        // For each possible move calculate the expected value from the state resulting from that move
        for (Move move : moves)
            expectedValues.put(move, getExpectedValue(player,state, move));
        
        // Return the map of values mapped to moves    
        return expectedValues;           
    }

    /**
     * Return the expected value of a game state for the player after making a move.
     */
    public double getExpectedValue(Player player, GameState state, Move move)
    {
        double expectedValue = 0;

        // If this is the end move, the state does not change.
        if (move.isEndOfTurn())
        {
            expectedValue = getExpectedValue(player, state);
        }
        // Calculate the expected value on the chances of winning losing and playing a draw with the values of the states as their result
        else
        {
            int attackingEyes = move.getAttackingCountry().getDice();
    	    int defendingEyes = move.getDefendingCountry().getDice();

            // win
            expectedValue += Chance.chanceTable(attackingEyes, defendingEyes) * getExpectedValue(player,state.expectedState(move, 1));
            // draw
            double drawChance = 1-Chance.chanceTable(attackingEyes, defendingEyes)-Chance.chanceTable(defendingEyes, attackingEyes);
            expectedValue += drawChance * getExpectedValue(player,state.expectedState(move, 2));
            // lose
            expectedValue += Chance.chanceTable(defendingEyes, attackingEyes) * getExpectedValue(player,state.expectedState(move, 3));
        }
        
        return expectedValue;
    }

    /**
     * Return the expected value of a game state for the player.
     */
    public double getExpectedValue(Player player, GameState state)
    {
        // Use describers to describe a gameState to values between -1 and 1 to use as input for the NN
        double[] input = encoder.encode(state, player);
		
        // Set the output of the descriptors as the input for the neural network
        network.getInput().setValues(input);
        
        // Let the network calculate a value
        network.forwardPropagate();

        // Return the output of the network
        double output = network.getOutput().getValue(0);

        return output;
    }

    public void adjustNetwork(Player player, GameState state, double awardedValue)
    {
        // double[] rewardValue;
        double[] targetValue = {awardedValue};
        double learningSpeed = 0.05;
        
        // First, call calcValueState so the network has the state inside its nodes
        double rewardValue = getExpectedValue(player, state);

        // then, backwardPropagate the correct output
        network.backPropagate(targetValue, learningSpeed);

        error.add(rewardValue - awardedValue);
    }

}
