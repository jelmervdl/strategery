package descriptors;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import game.Country;
import game.GameState;
import game.Player;

public class EnemyCountryBalance extends Descriptor
{
	/**
	 * Describe how equally the countries are divided among the enemy players.
	 */
	public double describe(GameState state, Player player)
	{
		// Count the number of countries for each player.
		Map<Player, Integer> numberOfCountries = new HashMap<Player, Integer>();
		int numCountries = 0;
		for (Country country : state.getCountries())
			if(country.player != player)
			{
				numberOfCountries.put(country.player,
						numberOfCountries.containsKey(country.player)
						? numberOfCountries.get(country.player) + 1
								: 1);
				numCountries++;
			}
		if (!numberOfCountries.isEmpty())
			return normalize((double) variance(numberOfCountries.values(), numCountries), 0, 1);
		else
			return normalize(1, 0, 1);
	}

	private double variance(Collection<Integer> numbers, int numCountries)
	{
		int sum = 0;
		for (Integer number : numbers)
			sum += number;

		double mean = (double) sum / numbers.size();

		double difference = 0;
		for (Integer number : numbers)
			difference += Math.abs(number - mean);
		int maxvariance = (numCountries/numbers.size() * (numbers.size()-1)) + (numCountries - numCountries/numbers.size());
		
		if (maxvariance > 0)
			return (double) difference / maxvariance;
		else
			return 1;
	}
}