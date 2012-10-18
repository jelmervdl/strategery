package game;

import java.util.List;

public interface GameEventListener
{
	public void onStep();

	public void onChooseMove(List<Move> moves);

	public void onMove(Move move);

	public void onStateChange(GameState state);

	public void onTurnStarted(Player player);

	public void onTurnEnded(Player player);

	public void onGameStarted(GameState state);

	public void onGameEnded(GameState state);

}