import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import java.util.List;

class Game
{
	GameState state;

	List<Player> players;

	public Game(List<Player> players, GameState state)
	{
		this.players = players;

		this.state = state;
	}

	public void step()
	{
		for (Player player : players)
		{
			if (!livingPlayers().contains(player))
				continue;

			Move move;

			do {
				List<Move> moves = state.generatePossibleMoves(player);

				System.out.println("Player " + player + " has possible moves:");
				for (Move aMove : moves)
					System.out.println("\t" + aMove);

				move = player.decide(moves, state);

				// move == null -> player finishes his turn.

				System.out.println("Player " + player + " will play move " + move);
				
				if (move != null)
					state = state.apply(move);
			} while(move != null);

			distributeNewDice(player);
		}
	}

	public List<Player> livingPlayers()
	{
		List<Player> alive = new Vector<Player>();

		for (Player player : players)
			if (state.getCountries(player).size() > 0)
				alive.add(player);

		return alive;
	}

	public void distributeNewDice(Player player)
	{
		distributeNewDice(player, Util.countLargestCluster(state.countries, player));
	}

	public void distributeNewDice(Player player, int dice)
	{
		List<Country> countries = state.getCountries(player);

		Collections.sort(countries, new Comparator<Country>(){
			public int compare(Country a, Country b)
			{
				return a.enemyNeighbours().size() - b.enemyNeighbours().size();
			}
		});

		while (dice > 0)
		{
			int diceAdded = 0;

			for (Country country : countries)
			{
				if (country.dice < country.maximumDice())
				{
					country.dice++;
					dice--;
					diceAdded++;
				}

				if (dice == 0)
					break;
			}

			// Stop if all countries are filled
			if (diceAdded == 0)
				break;
		}
	}
}
