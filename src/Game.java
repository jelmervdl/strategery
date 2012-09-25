import java.util.Vector;

class Game
{
	GameState state;

	Vector<Player> players;

	public Game(Vector<Player> players, GameState state)
	{
		this.players = players;

		this.state = state;
	}

	public void step()
	{
		for (Player player : players)
		{
			Move move;

			do {
				Vector<Move> moves = state.generatePossibleMoves(player);

				System.out.println("Player " + player + " has possible moves:");
				for (Move aMove : moves)
					System.out.println("\t" + aMove);

				move = player.decide(moves, state);

				// move == null -> player finishes his turn.

				System.out.println("Player " + player + " will play move " + move);
				
				if (move != null)
					state = state.apply(move);

				for (Player player2 : players)
					if (state.getCountries(player2).size() == 0)
						System.out.println("Player " + player2 + " has been defeated!");

			} while(move != null);

			distributeNewDice(player);
		}
	}

	private void distributeNewDice(Player player)
	{
		int dice = Util.countLargestCluster(state.countries, player);

		// to do something
	}
}
