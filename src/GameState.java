import java.util.Random;
import java.util.Vector;

class GameState
{
	Vector<Country> countries;

	public GameState()
	{
		this.countries = new Vector<Country>();
	}

	public GameState(Vector<Country> countries)
	{
		this.countries = countries;
	}

	public void apply(Move move)
	{
		GameState state = this.clone();

		// fight battle (if there is one)
		int attackingEyes = rollDice(move.attackingCountry.dice);
		int defendingEyes = rollDice(move.defendingCountry.dice);

		// Attacker wins
		if (attackingEyes > defendingEyes)
		{
			// Take the country!
			state.getCountry(move.defendingCountry).player = move.attackingCountry.player;

			// Assign remaining dice to country
			state.getCountry(move.defendingCountry).dice = remainingDice(attackingEyes - defendingEyes);
		}

		// It's a draw
		else if (attackingEyes == defendingEyes)
		{
			state.getCountry(move.attackingCountry).dice = 1;

			state.getCountry(move.defendingCountry).dice = 1;
		}

		// Attacker loses
		else
		{
			state.getCountry(move.attackingCountry).dice = 1;

			state.getCountry(move.defendingCountry).dice = remainingDice(attackingEyes - defendingEyes);
		}
	}

	public Vector<Move> generatePossibleMoves(Player player)
	{
		Vector<Move> moves = new Vector<Move>();

		for (Country country : countries)
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

	public GameState clone()
	{
		GameState state = new GameState();

		// Copy all the countries
		for (Country country : countries)
			state.countries.add(country.clone());

		// Update neighbours to direct to the countries in the current state
		for (Country country : state.countries)
			for (int i = 0; i < country.neighbours.size(); ++i)
				country.neighbours.set(i, state.getCountry(country.neighbours.get(i)));

		return state;
	}

	public Country getCountry(Country country)
	{
		return countries.get(countries.indexOf(country));
	}

	private int rollDice(int dice)
	{
		Random random = new Random();

		int eyes = 0;

		for (int i = 0; i < dice; ++i)
			eyes += 1 + random.nextInt(6);

		return eyes;
	}

	private int remainingDice(int eyes)
	{
		return (int) Math.ceil(eyes / 6.0);
	}
}