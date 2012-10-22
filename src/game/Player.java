package game;

import java.awt.Color;

import java.util.List;

public interface Player
{
	public Color getColor();

	public Move decide(List<Move> possibleMoves, GameState state);	
}
