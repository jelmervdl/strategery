package game;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import java.util.List;

public class Game implements Runnable
{
	final static public int MAX_NUMBER_OF_TURNS = 1000;

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
		// alive can be set to false to end this Java thread.
		alive = true;

		// Give each player 10 dice to start with
		for (Player player : players)
			distributeNewDice(player, 10);
	
		publishStartOfGame(state);

		// Keep track of the number of turns played (to kill the game if we 
		// end up in a loop)
		int turn = 0;

		// While there is no winner (and the thread hasn't been killed): play!
		while (alive && !state.isFinished() && ++turn < MAX_NUMBER_OF_TURNS)
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

		// For each player (using our List here, and not the getPlayers() set
		// because the order of the players has to be maintained.)
		for (Player player : players)
		{
			// If the player is dead, it can be skipped
			if (!livingPlayers().contains(player))
				continue;

			publishStartOfTurn(player);

			Move move;

			// While the player plays moves (other than end-of-turn)
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
		List<Player> living = new Vector<Player>();

		for (Player player : players)
			if (state.getCountries(player).size() > 0)
				living.add(player);

		return living;
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

				if (country.getDice() < country.getMaximumDice())
				{
					country.setDice(country.getDice() + 1);
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
