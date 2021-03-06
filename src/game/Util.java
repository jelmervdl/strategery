package game;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class Util
{
	static public int countLargestCluster(List<Country> countries, Player player)
	{
		int largestClusterSize = 0;
		Set<Country> countedCountries = new HashSet<Country>();

		for (Country country : countries)
		{
			if (!country.getPlayer().equals(player))
				continue;

			if (countedCountries.contains(country))
				continue;

			int clusterSize = countCluster(country, countedCountries);

			if (clusterSize > largestClusterSize)
				largestClusterSize = clusterSize;
		}

		return largestClusterSize;
	}

	static public int countCluster(Country country, Set<Country> countedCountries)
	{
		int count = 1;

		countedCountries.add(country);

		for (Country neighbour : country.neighbours)
			if (neighbour.getPlayer().equals(country.getPlayer()))
				if (!countedCountries.contains(neighbour))
					count += countCluster(neighbour, countedCountries);

		return count;
	}
}
