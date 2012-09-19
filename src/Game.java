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
				Vector<Move> moves = generatePossibleMoves(player);

				move = player.decide(moves, state);
				
				if (move != null)
					state.apply(move);
			} while(move != null);

			distributeNewDice(player);
		}
	}

	private Vector<Move> generatePossibleMoves(Player player)
	{
		Vector<Move> moves = new Vector<Move>();

		for (Country country : state.countries)
		{
			if (country.player != player)
				continue;

			if (country.dice <= 1)
				continue;

			Vector<Country> enemyNeighbours = country.enemyNeighbours();

			if (enemyNeighbours.size() == 0)
				continue;

			for (Country enemyCountry : enemyNeighbours)
				moves.add(new Move(country, enemyCountry));
		}

		return moves; 
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
