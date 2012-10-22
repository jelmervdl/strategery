package td;

import java.util.*;

import game.GameState;
import game.Move;
import game.PlayerAdapter;

public class TDPlayer extends PlayerAdapter
{
    private TDLearning td;
    
	public TDPlayer(String name)
    {
		super(name);
        td = new TDLearning();
	}


	public Move decide(List<Move> possibleMoves, GameState state)
	{
        HashMap<Move, Double> possibleStates = new HashMap<Move, Double>();		
        // Calculate the value of the states resulting from the possible moves.        
        possibleStates = td.mapValueToMove(this, state, possibleMoves);
        // From the list of values mapped to the moves select the best move according to policy
        Move move = selectAction(possibleStates);
        // Adjust the NN for the move it just did.        
        td.adjustNetwork(move, possibleStates.get(move), state);       
        
        // return the move that is to be executed
        return move;
    }
    public Move selectAction(HashMap<Move, Double> possibleStates)
    {
        Move move = null;
        double max = 0;
        // apply policy
        for (Map.Entry<Move, Double> pair : possibleStates.entrySet())
		 	if(pair.getValue()>max)
            {
                max = pair.getValue();
                move = pair.getKey();
            }        
        return move;
        
    }
}
