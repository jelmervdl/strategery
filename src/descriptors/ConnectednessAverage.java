package descriptors;

import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import game.RandomPlayer;
import game.Country;
import game.GameState;
import game.Player;
import game.Util;

public class ConnectednessAverage extends Descriptor
{
	/**
	 * Describe how large my cluster is compared to the average cluster size
	 * of enemies.
	 */
	public double describe(GameState state, Player player)
	{
		Map<Player, Integer> largestClusterSize = new HashMap<Player, Integer>();
		
		Set<Country> countedCountries = new HashSet<Country>();

		for (Country country : state.getCountries())
		{
			if (countedCountries.contains(country))
				continue;

			int clusterSize = Util.countCluster(country, countedCountries);

			if (largestClusterSize.containsKey(country.getPlayer()))
			{
				if(largestClusterSize.get(country.getPlayer()) < clusterSize)
					largestClusterSize.put(country.getPlayer(), clusterSize);
			}
			else
			{
					largestClusterSize.put(country.getPlayer(), clusterSize);
			}
		}
		
		if ( !largestClusterSize.containsKey(player) )
			return -1;		
		
		int playerCount = 0;
		while(largestClusterSize.size() < state.getNumberOfPlayers())
			largestClusterSize.put(new RandomPlayer("empty"+playerCount++), 0);
		
		int sum = 0;
		for(Integer clusterSize : largestClusterSize.values())
		{
			sum += clusterSize;
		}
		sum -= largestClusterSize.get(player);
		if (sum == 0)
			return 1;
		double mean = (double)sum / (largestClusterSize.size() - 1);
		
		double ratio = (double)(largestClusterSize.get(player)) / (double)mean;

		if(ratio > state.getNumberOfPlayers()-1)
			ratio = state.getNumberOfPlayers()-1;

		return normalize(ratio, 0, state.getNumberOfPlayers()-1);
	}
}