package descriptors;

import java.util.List;
import java.util.Vector;

import game.GameState;
import game.Player;

public class Mixed extends Descriptor
{
	private List<Descriptor> descriptors;
	
	public Mixed()
	{
		descriptors = new Vector<Descriptor>();

		descriptors.add(new Dominance());
		descriptors.add(new CountryBalance());
	}

	public double describe(GameState state, Player player)
	{
		double sum = 0.0;

		for (Descriptor descriptor : descriptors)
			sum += descriptor.describe(state, player);

		return sum / descriptors.size();
	}
}
