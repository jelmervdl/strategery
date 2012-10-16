package descriptors;

import game.GameState;
import game.Player;

public abstract class Descriptor
{
	abstract public double describe(GameState state, Player player);

	public String toString()
	{
		return "[Descriptor " + getClass().getName() + "]";
	}

	/**
	 * Normalize a value in range (min,max) to (-1, 1).
	 */
	protected double normalize(double value, double min, double max)
	{
		return ((value / (max - min)) * 2) - 1;
	}
}