package td;

import java.util.*;
import java.io.*;

import game.GameState;
import game.Move;
import game.Player;
import td.Chance;

class TDLearning
{
    public HashMap<Move, Double> mapValueToMove(Player player, GameState state, List<Move> moves)
    {
        // Initialize HashMap where the values get mapped to the possible moves
        HashMap<Move, Double> expectedValues = new HashMap<Move, Double>();        
        List<GameState> states = new Vector<GameState>();
       
        // For each possible move calculate the expected value from the state resulting from that move
        for (Move move : moves)
            expectedValues.put(move, getExpectedValueState(state, move));
        
        // Return the map of values mapped to moves    
        return expectedValues;           
    }

    public double getExpectedValueState(GameState state, Move move)
    {
        
        int attackingEyes = move.attackingCountry.dice;
	    int defendingEyes = move.defendingCountry.dice;
        double expectedValue = 0;

        // Calculate the expected value on the chances of winning losing and playing a draw with the values of the states as their result

        //win
        expectedValue += Chance.chanceTable(attackingEyes, defendingEyes) * calcValueState(state.expectedState(move, 1));
        //draw
        double drawChance = 1-Chance.chanceTable(attackingEyes, defendingEyes)-Chance.chanceTable(defendingEyes, attackingEyes);
        expectedValue += drawChance * calcValueState(state.expectedState(move, 2));
        //lose        
        expectedValue += Chance.chanceTable(defendingEyes, attackingEyes) * calcValueState(state.expectedState(move, 3));
        
        return expectedValue;    
    }

    public double calcValueState(GameState state)
    {
        //NN calculeert de value van een gamestate
        return 0;
    }

    public void adjustNetwork(Move move, Double expectedValue, GameState state)
    {
        double rewardValue;
        double targetValue;
        
        //trainNN(state, targetValue);
    }

}
