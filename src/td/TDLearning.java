package td;

import java.util.*;
import java.io.*;

import game.GameState;
import game.Move;
import game.Player;

class TDLearning
{
    private GameStateEncoder encoder;

    public TDLearning()
    {
        encoder = GameStateEncoder.buildDefaultEncoder();
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
		    

        //NN calculeert de value van een gamestates
        

        return 0;
    }

    public void adjustNetwork(Move move, Double expectedValue, GameState state)
    {
        double rewardValue;
        double targetValue;
        
        //trainNN(state, targetValue);
    }

}
