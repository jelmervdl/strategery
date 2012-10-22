package td;

import java.util.*;
import java.io.*;

import game.GameState;
import game.Move;
import game.Player;

class TDLearning
{
    public HashMap<Move, Double> getMoves(Player player, GameState state)
    {
        HashMap<Move, Double> expectedValues = new HashMap<Move, Double>();        
        List<GameState> states = new Vector<GameState>();
        List<Move> moves = state.generatePossibleMoves(player);

        for (Move move : moves)
            expectedValues.put(move, getValueState(state.apply(move, true)));
        
        return expectedValues;           
    }

    public double getValueState(GameState possibleState)
    {
        //NN
        return 0;    
    }
    
    public void adjustNetwork(Move move, Double expectedValue, GameState state)
    {
        double rewardValue = getValueState(state.apply(move,false));
        double targetValue = rewardValue + expectedValue;
        
        //trainNN(state, targetValue);
    }
}
