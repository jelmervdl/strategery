package descriptors;

import java.util.HashSet;
import java.util.Set;

import game.Country;
import game.GameState;
import game.Player;
import game.Util;

public class Connectedness extends Descriptor
{
	/**
	 * Describe how large my cluster is compared to the largest cluster of
	 * countries on the map.
	 */
	public double describe(GameState state, Player player)
	{
		int largestClusterSize = 0;
		int myLargestClusterSize = 0;

		Set<Country> countedCountries = new HashSet<Country>();

		for (Country country : state.getCountries())
		{
			if (countedCountries.contains(country))
				continue;

			int clusterSize = Util.countCluster(country, countedCountries);

			if (clusterSize > largestClusterSize)
				largestClusterSize = clusterSize;

			if (country.getPlayer().equals(player))
				if (clusterSize > myLargestClusterSize)
					myLargestClusterSize = clusterSize;
		}

		return normalize((double) myLargestClusterSize / largestClusterSize, 0, 1);
	}
}