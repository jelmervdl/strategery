package descriptors;

import java.util.List;
import java.util.Vector;

import game.GameState;
import game.Player;

public class CombinedDominance extends Descriptor
{
	Descriptor dom = new Dominance();
	Descriptor diceDom = new DiceDominance();
	double diceParam = 0.6;
	
	public CombinedDominance()
	{
	}

	public double describe(GameState state, Player player)
	{
		double dominance = dom.describe(state, player);
		double diceDominance = diceDom.describe(state,player);
		
		return (diceParam * (1 - (dominance/2+0.5) * (diceDominance/2+0.5)) + (dominance/2+0.5) - 0.5 ) * 2.0;
	}
}
