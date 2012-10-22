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
		for (Country country : state.getCountries())
			if(country.player == player)
			{
				numberOfCountries.put(country.player,
						numberOfCountries.containsKey(country.player)
						? numberOfCountries.get(country.player) + 1
								: 1);
			}

		return normalize((double) variance(numberOfCountries.values()) / state.getCountries().size(), 0, 1);
	}

	private double variance(Collection<Integer> numbers)
	{
		int sum = 0;
		for (Integer number : numbers)
			sum += number;

		double mean = (double) sum / numbers.size();

		double difference = 0;
		for (Integer number : numbers)
			difference += Math.abs(number - mean);

		return (double) difference / numbers.size();
	}
}