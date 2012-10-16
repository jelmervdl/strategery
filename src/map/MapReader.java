package map;

import java.io.*;
import java.util.*;

import game.Country;
import game.Player;

public class MapReader
{
	private class CountryContainer
	{
		public Country country;

		public List<Integer> neighbours;

		public CountryContainer(Player player)
		{
			this.country = new Country(player, 0);

			this.neighbours = new Vector<Integer>();
		}
	}

	private List<Player> players;

	public MapReader(List<Player> players)
	{
		this.players = players;
	}

	public List<Country> read(String file) throws IOException
	{
		Map<Integer, CountryContainer> countryMap = new HashMap<Integer, CountryContainer>();

		FileInputStream fstream = new FileInputStream(file);
	 
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		String line;
		while ((line = br.readLine()) != null)  
		{
			// Skip comments and white lines.
			if (line.startsWith("#") || line.trim().equals(""))
				continue;

			Scanner scanner = new Scanner(line);

			// Index 0: country id
			Integer countryId = scanner.nextInt();

			// Index 1: player id
			Integer playerId = scanner.nextInt();
			Player player = players.get(playerId - 1);

			CountryContainer country = new CountryContainer(player);

			countryMap.put(countryId, country);

			// Rest of the numbers: neighbouring country ids.
			while (scanner.hasNextInt())
				country.neighbours.add(scanner.nextInt());
		}

		List<Country> countries = new Vector<Country>();

		// Connect all the countries to their neighbours, and filter them into a list of countries.
		for (CountryContainer container : countryMap.values())
		{
			for (Integer countryId : container.neighbours)
				container.country.neighbours.add(countryMap.get(countryId).country);

			countries.add(container.country);
		}

		return countries;
	}
}
