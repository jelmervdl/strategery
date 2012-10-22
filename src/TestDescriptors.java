import java.util.*;
import java.io.File;
import java.io.IOException;
import descriptors.Descriptor;
import game.*;
import map.MapReader;

public class TestDescriptors
{
	static public void main(String[] args)
	{
		List<Player> players = new Vector<Player>();
		players.add(new RandomPlayer("a"));
		players.add(new RandomPlayer("b"));
		players.add(new RandomPlayer("c"));
		players.add(new RandomPlayer("d"));

		List<Descriptor> descriptors = new Vector<Descriptor>();
		descriptors.add(new descriptors.Dominance());
		descriptors.add(new descriptors.StrengthOnBorders());
		descriptors.add(new descriptors.Connectedness());
		descriptors.add(new descriptors.ConnectedBalance());
		descriptors.add(new descriptors.CountryBalance());
		descriptors.add(new descriptors.EnemyCountryBalance());
		descriptors.add(new descriptors.EnemyDiceBalance());

		MapReader reader = new MapReader(players);

		String path = args.length > 0
			? args[0]
			: "../maps/1.txt";

		try {
			List<Country> countries = reader.read(new File(path));
		
			GameState state = new GameState(countries);

			for (Player player : players)
			{
				System.out.println(player);
				for (Descriptor descriptor : descriptors)
					System.out.println(descriptor + "\t" + descriptor.describe(state, player));

				System.out.println("");
			}
		}
		catch (IOException e) {
			System.out.println("Could not read map: " + e.getMessage());
			return;
		}
	}
}
