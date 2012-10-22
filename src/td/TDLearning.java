package td;

import java.util.*;
import java.io.*;

import game.GameState;
import game.Move;
import game.Player;
import td.Chance;

class TDLearning
{
    public HashMap<Move, Double> getMoves(Player player, GameState state)
    {
        HashMap<Move, Double> expectedValues = new HashMap<Move, Double>();        
        List<GameState> states = new Vector<GameState>();
        List<Move> moves = state.generatePossibleMoves(player);

        for (Move move : moves)
            expectedValues.put(move, getExpectedValueState(state, move));
            
        return expectedValues;           
    }

    public double getExpectedValueState(GameState state, Move move)
    {
        int attackingEyes = move.attackingCountry.dice;
	    int defendingEyes = move.defendingCountry.dice;
        double expectedValue = 0;

        //win
        expectedValue += Chance.chanceTable(attackingEyes, defendingEyes) * calcValue(state.expectedState(move, 1));
        //draw
        expectedValue += 1-Chance.chanceTable(attackingEyes, defendingEyes)-Chance.chanceTable(defendingEyes, attackingEyes) * calcValue(state.expectedState(move, 2));
        //lose        
        expectedValue += Chance.chanceTable(defendingEyes, attackingEyes) * calcValue(state.expectedState(move, 3));
        
        return expectedValue;    
    }
    
    public void adjustNetwork(Move move, Double expectedValue, GameState state)
    {
        double rewardValue = getExpectedValueState(state, move);
        double targetValue = rewardValue + expectedValue;
        
        //trainNN(state, targetValue);
    }
    public double calcValue(GameState state)
    {
        return 0;
    }
}
