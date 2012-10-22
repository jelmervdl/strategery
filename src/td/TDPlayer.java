package td;

import java.util.*;

import game.GameState;
import game.Move;
import game.Player;

public class TDPlayer extends Player
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
        possibleStates = td.getMoves(this, state);
        Move move = selectAction(possibleStates);        
        td.adjustNetwork(move, possibleStates.get(move), state);       
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
