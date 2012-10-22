package descriptors;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import game.Country;
import game.GameState;
import game.Player;

public class EnemyDiceBalance extends Descriptor
{
	/**
	 * Describe how equally the dices are distributed among the enemy players.
	 */
	public double describe(GameState state, Player player)
	{
		// Count the number of countries for each player.
		Map<Player, Integer> numberOfDices = new HashMap<Player, Integer>();
		for (Country country : state.getCountries())
			if(country.player != player)
			{
				numberOfDices.put(country.player,
						numberOfDices.containsKey(country.player)
						? numberOfDices.get(country.player) + country.dice
								: country.dice);
			}
		
		if (!numberOfDices.isEmpty())
			return normalize((double) variance(numberOfDices.values()), 0, 1);
		else
			return normalize(1, 0, 1);
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
		
		double maxvariance = mean * (numbers.size()-2) + sum;
		
		if (maxvariance > 0)
			return (double) difference / maxvariance;
		else
			return 1;
	}
}