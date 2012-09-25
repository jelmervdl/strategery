import java.util.Vector;

class Game
{
	GameState state;

	Vector<Player> players;

	public Game(Vector<Player> players)
	{
		this.players = players;

		this.state = generateNewGame();
	}

	public void step()
	{
		for (Player player : players)
		{
			Move move;

			do {
				Vector<Move> moves = state.generatePossibleMoves(player);

				move = player.decide(moves, state);
				
				if (move != null)
					state.apply(move);
			} while(move != null);

			distributeNewDice(player);
		}
	}

	private void distributeNewDice(Player player)
	{
		int dice = Util.countLargestCluster(state.countries, player);

		// to do something
	}

	private GameState generateNewGame()
	{
		// generate random countries, assign players to countries.

		// assign dice to coutries

		return new GameState();
	}
}
