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
        List<GameState> states = new Vector<GameState>();		
        states = td.getMoves(this, state);
        System.out.println(states);
        
        return possibleMoves.get(0);
    }
}
