package map;

import java.io.*;
import java.util.*;

import game.Country;
import game.Player;
import map.Hexagon;

public class MapWriter
{
	public void write(List<Country> countries, File file) throws IOException
	{
		PrintWriter out = new PrintWriter(file);

		out.println("# Dumping " + countries.size() + " countries");

		List<Player> players = collectPlayers(countries);

		out.println("# " + players.size() + " players");
		
		for (Country country : countries)
		{
			// id
			out.print(getIndex(countries, country));

			// player id
			out.print(" " + getIndex(players, country.player));

			// neighbouring countries
			for (Country neighbour : country.getNeighbours())
				out.print(" " + getIndex(countries, neighbour));

			if (country.getHexagons().size() > 0)
			{
				out.print(" ;");

				for (Hexagon hexagon : country.getHexagons())
					out.print(" " + hexagon.x + " " + hexagon.y);
			}

			out.println();
		}

		out.close();
	}

	private List<Player> collectPlayers(List<Country> countries)
	{
		Set<Player> players = new HashSet<Player>();

		for (Country country : countries)
			players.add(country.getPlayer());

		return new Vector<Player>(players);
	}

	static private <T> int getIndex(List<T> haystack, T needle)
	{
		for (int i = 0; i < haystack.size(); ++i)
			if (haystack.get(i).equals(needle))
				return i;

		throw new RuntimeException("Could not find element in haystack");
	}
}
