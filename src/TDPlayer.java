import java.util.Random;
import java.util.List;
import java.util.*;

class TDPlayer extends Player
{
    TDLearning td;
    
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
