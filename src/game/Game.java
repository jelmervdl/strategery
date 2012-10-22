package game;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import java.util.List;

public class Game implements Runnable
{
	GameState state;

	List<Player> players;

	List<GameEventListener> listeners;

	boolean alive;

	public Game(List<Player> players, GameState state)
	{
		this.state = state;

		this.players = players;

		this.listeners = new Vector<GameEventListener>();
	}

	public void addEventListener(GameEventListener listener)
	{
		listeners.add(listener);
	}

	public void removeEventListener(GameEventListener listener)
	{
		listeners.remove(listener);
	}

	public GameState getState()
	{
		return state;
	}

	public void run()
	{
		alive = true;

		for (Player player : players)
			distributeNewDice(player, 10);
	
		publishStartOfGame(state);

		while (alive && livingPlayers().size() > 1)
			step();

		publishEndOfGame(state);
	}

	public void stop()
	{
		alive = false;
	}

	protected void step()
	{
		publishStep();

		for (Player player : players)
		{
			if (!livingPlayers().contains(player))
				continue;

			publishStartOfTurn(player);

			Move move;

			do {
				List<Move> moves = state.generatePossibleMoves(player);

				publishPossibleMoves(moves);
				
				move = player.decide(moves, state);

				publishMove(move);
				
				GameState result = state.apply(move, false);
				
				player.feedback(state, move, result);

				setState(result);
			} while (alive && !move.isEndOfTurn());

			publishEndOfTurn(player);

			distributeNewDice(player);

			publishState(state);
		}
	}

	protected List<Player> livingPlayers()
	{
		List<Player> alive = new Vector<Player>();

		for (Player player : players)
			if (state.getCountries(player).size() > 0)
				alive.add(player);

		return alive;
	}

	protected void distributeNewDice(Player player)
	{
		distributeNewDice(player, Util.countLargestCluster(state.countries, player));
	}

	protected void distributeNewDice(Player player, int dice)
	{
		List<Country> countries = state.getCountries(player);

		Collections.sort(countries, new Comparator<Country>(){
			public int compare(Country a, Country b)
			{
				return a.enemyNeighbours().size() - b.enemyNeighbours().size();
			}
		});

		boolean skipNonBorderCountries = true;

		while (dice > 0)
		{
			int diceAdded = 0;

			for (Country country : countries)
			{
				if (skipNonBorderCountries && country.enemyNeighbours().size() == 0)
					continue;

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
				if (skipNonBorderCountries)
					skipNonBorderCountries = false;
				else
					break;
		}
	}

	protected void setState(GameState state)
	{
		this.state = state;
		publishState(state);
	}
	
	protected void publishStep()
	{
		for (GameEventListener listener : listeners)
			listener.onStep();
	}

	protected void publishPossibleMoves(List<Move> moves)
	{
		for (GameEventListener listener : listeners)
			listener.onChooseMove(moves);
	}

	protected void publishMove(Move move)
	{
		for (GameEventListener listener : listeners)
			listener.onMove(move);
	}

	protected void publishState(GameState state)
	{
		for (GameEventListener listener : listeners)
			listener.onStateChange(state);
	}

	protected void publishStartOfTurn(Player player)
	{
		for (GameEventListener listener : listeners)
			listener.onTurnStarted(player);
	}

	protected void publishEndOfTurn(Player player)
	{
		for (GameEventListener listener : listeners)
			listener.onTurnEnded(player);
	}

	protected void publishStartOfGame(GameState state)
	{
		for (GameEventListener listener : listeners)
			listener.onGameStarted(state);
	}

	protected void publishEndOfGame(GameState state)
	{
		for (GameEventListener listener : listeners)
			listener.onGameEnded(state);
	}
}
