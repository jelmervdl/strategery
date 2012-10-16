package descriptors;

import game.Country;
import game.GameState;
import game.Player;

public class StrengthOnBorders extends Descriptor
{
	public double describe(GameState state, Player player)
	{
		double sum = 0;
		int count = 0;

		// For all my countries
		for (Country country : state.getCountries(player))
		{
			// calculate how much I dominate the enemy neighbours
			for (Country neighbour : country.enemyNeighbours())
			{
				sum += calculateDominanceBetween(country, neighbour);
				count += 1;
			}
		}

		return sum / count;
	}

	/**
	 * Returns dominance over other country in range from -1 to 1.
	 */
	private double calculateDominanceBetween(Country me, Country other)
	{
		int max = Math.max(me.maximumDice(), other.maximumDice());

		return (double) (me.dice - other.dice) / max;
	}
} 