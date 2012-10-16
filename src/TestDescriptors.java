import java.util.*;
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
		players.add(new RandomPlayer("e"));

		List<Descriptor> descriptors = new Vector<Descriptor>();
		descriptors.add(new descriptors.Dominance());

		MapReader reader = new MapReader(players);

		String path = args.length > 0
			? args[0]
			: "../maps/1.txt";

		try {
			List<Country> countries = reader.read(path);
		
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