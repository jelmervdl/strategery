package descriptors;

import game.GameState;
import game.Country;
import game.Player;

public class DiceDominance extends Descriptor
{
	public double describe(GameState state, Player player)
	{
		int myDice = 0;
		int totalDice = 0;
		
		for(Country country : state.getCountries())
		{
			if(country.getPlayer() == player)
				myDice += country.getDice();
			totalDice += country.getDice();
		}
		
		return normalize((double)myDice / totalDice, 0, 1);
	}
} 