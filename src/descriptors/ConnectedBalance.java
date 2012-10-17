package descriptors;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import game.Country;
import game.GameState;
import game.Player;
import game.Util;

public class ConnectedBalance extends Descriptor
{
	/**
	 * Describe how large my cluster is compared to the largest cluster of
	 * countries on the map.
	 */
	public double describe(GameState state, Player player)
	{
		Map<Player, Integer> largestClusterSizes = new HashMap<Player, Integer>();
		Set<Country> countedCountries = new HashSet<Country>();

		for (Country country : state.getCountries())
		{
			if (countedCountries.contains(country))
				continue;

			int clusterSize = Util.countCluster(country, countedCountries);

			if (!largestClusterSizes.containsKey(country.player) || clusterSize > largestClusterSizes.get(country.player))
				largestClusterSizes.put(country.player, clusterSize);
		}

		return normalize((double) variance(largestClusterSizes.values()) / state.getCountries().size(), 0, 1);
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