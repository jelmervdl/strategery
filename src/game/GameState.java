package game;

import java.util.Random;
import java.util.List;
import java.util.Vector;

public class GameState
{
	List<Country> countries;

	public GameState()
	{
		this.countries = new Vector<Country>();
	}

	public GameState(List<Country> countries)
	{
		this.countries = countries;
	}

	public GameState(GameState other)
	{
		countries = new Vector<Country>();

		// Copy all the countries
		for (Country country : other.countries)
			countries.add(new Country(country));

		// Update neighbours to direct to the countries in the current state
		for (Country country : countries)
			for (int i = 0; i < country.neighbours.size(); ++i)
				country.neighbours.set(i, getCountry(country.neighbours.get(i)));
	}

	/**
	 * Make a move, and generate the resulting state. If expected is true, it will
	 * calculate the most likely outcome. Otherwise, it will actually play the game
	 * with dice and everything.
	 */
	public GameState apply(Move move, Boolean expected)
	{
		// Clone the current gamestate.
		GameState state = new GameState(this);

        int attackingEyes;
        int defendingEyes;

        // If we calculate the expected value, don't add the element of chance.
        if(expected)
        {
            attackingEyes = move.attackingCountry.dice;
		    defendingEyes = move.defendingCountry.dice;
        }
        // Otherwise, let's roll those dice.
        else
        {
		    attackingEyes = rollDice(move.attackingCountry.dice);
		    defendingEyes = rollDice(move.defendingCountry.dice);
        }

		// Attacker wins
		if (attackingEyes > defendingEyes)
		{
			// Take the country!
			state.getCountry(move.defendingCountry).player = move.attackingCountry.player;

			// Reset attacking country dice to 1 (as the army has moved to the defending country)
			state.getCountry(move.attackingCountry).dice = 1;

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

			state.getCountry(move.defendingCountry).dice = remainingDice(defendingEyes - attackingEyes);
		}

		return state;
	}

	public List<Move> generatePossibleMoves(Player player)
	{
		List<Move> moves = new Vector<Move>();

		for (Country country : countries)
		{
			if (country.player != player)
			{
				// System.out.println("Not same player");
				continue;
			}

			if (country.dice <= 1)
			{
				// System.out.println("Not enough dice");
				continue;
			}

			List<Country> enemyNeighbours = country.enemyNeighbours();

			if (enemyNeighbours.size() == 0)
			{
				// System.out.println("Not enough enemy neighbours");
				continue;
			}

			for (Country enemyCountry : enemyNeighbours)
				moves.add(new Move(country, enemyCountry));
		}

		return moves; 
	}

	public Country getCountry(Country country)
	{
		return countries.get(countries.indexOf(country));
	}

	public List<Country> getCountries()
	{
		return countries;
	}

	public List<Country> getCountries(Player player)
	{
		List<Country> playerCountries = new Vector<Country>();

		for (Country country : countries)
			if (country.player == player)
				playerCountries.add(country);

		return playerCountries;
	}

	public String toString()
	{
		String out = "[GameState countries:\n";

		for (Country country : countries)
			out += "\t" + country + "\n";

		out += "]";

		return out;
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
		return (int) Math.max(1,Math.round(eyes / 6.0));
	}
}
