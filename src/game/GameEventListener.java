package game;

public interface GameEventListener
{
	public void onStep();

	public void onMove(Move move);

	public void onStateChange(GameState state);

	public void onTurnStarted(Player player);

	public void onTurnEnded(Player player);

	public void onGameStarted(GameState state);

	public void onGameEnded(GameState state);

}