package descriptors;

import game.GameState;
import game.Player;

public class Dominance extends Descriptor
{
	public double describe(GameState state, Player player)
	{
		return normalize((double) state.getCountries(player).size() / state.getCountries().size(), 0, 1);
	}
} 