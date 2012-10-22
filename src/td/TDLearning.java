package td;

import java.util.*;
import java.io.*;

import game.GameState;
import game.Move;
import game.Player;

import neuralnetwork.Layer;
import neuralnetwork.NeuralNetwork;

class TDLearning
{
    private GameStateEncoder encoder;

    private NeuralNetwork network;

    public TDLearning()
    {
        // The encoder turns a GameState instance into a set of doubles.
        encoder = GameStateEncoder.buildDefaultEncoder();

        // Configuration of layers of the neural network
        int dimensions[] = new int[] {
            encoder.getDescriptors().size(), // input layer
            10, // hidden layer
            1 // output layer
        };

        network = new NeuralNetwork(dimensions);
    }

    public HashMap<Move, Double> mapValueToMove(Player player, GameState state, List<Move> moves)
    {
        // Initialize HashMap where the values get mapped to the possible moves
        HashMap<Move, Double> expectedValues = new HashMap<Move, Double>();        
        List<GameState> states = new Vector<GameState>();
       
        // For each possible move calculate the expected value from the state resulting from that move
        for (Move move : moves)
            expectedValues.put(move, getExpectedValueState(player,state, move));
        
        // Return the map of values mapped to moves    
        return expectedValues;           
    }

    public double getExpectedValueState(Player player, GameState state, Move move)
    {
        
        int attackingEyes = move.attackingCountry.dice;
	    int defendingEyes = move.defendingCountry.dice;
        double expectedValue = 0;

        // Calculate the expected value on the chances of winning losing and playing a draw with the values of the states as their result

        //win
        expectedValue += Chance.chanceTable(attackingEyes, defendingEyes) * calcValueState(player,state.expectedState(move, 1));
        //draw
        double drawChance = 1-Chance.chanceTable(attackingEyes, defendingEyes)-Chance.chanceTable(defendingEyes, attackingEyes);
        expectedValue += drawChance * calcValueState(player,state.expectedState(move, 2));
        //lose        
        expectedValue += Chance.chanceTable(defendingEyes, attackingEyes) * calcValueState(player,state.expectedState(move, 3));
        
        return expectedValue;    
    }

    public double calcValueState(Player player, GameState state)
    {
        // Use describers to describe a gameState to values between -1 and 1 to use as input for the NN
        double[] input = encoder.encode(state, player);
		
        // Set the output of the descriptors as the input for the neural network
        network.getInput().setValues(input);
        
        // Let the network calculate a value
        network.forwardPropagate();

        // Return the output of the network
        return network.getOutput().getValue(0);
    }

    public void adjustNetwork(Move move, Double expectedValue, GameState state)
    {
        double rewardValue;
        double targetValue;
        
        // First, call calcValueState so the network has the state inside its nodes
        // calcValueState()

        // then, backwardPropagate the correct output
        // network.backwardPropagate({targetValue}, learningSpeed);
    }

}
