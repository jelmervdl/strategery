package td;

import java.util.*;
import java.io.*;

import game.GameState;
import game.Move;
import game.Player;

class TDLearning
{
    public List<GameState> getMoves(Player player, GameState state)
    {
        List<GameState> states = new Vector<GameState>();
        List<Move> moves = state.generatePossibleMoves(player);

        for (Move move : moves)
            states.add(state.apply(move, true));
            
        return states;   
    }
}
