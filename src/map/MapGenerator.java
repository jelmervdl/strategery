package map;

import java.util.List;
import java.util.Random;
import java.util.Vector;

import game.Country;
import game.GameState;
import game.Player;

public class MapGenerator
{
	private List<Player> players;

	public MapGenerator(List<Player> players)
	{
		this.players = players;
	}
	
	/**
	 * nCountries: Number of countries per player
	 * players: Participating players
	 * nBorders: Average number of borders required per country
	 */
	public GameState generate(int nCountries, double nBorders)
	{
		int nPlayers = players.size();
		int totalCountries = nCountries * nPlayers;
		int gridSize = (int)(Math.sqrt(totalCountries) * 4);
		Hexagon[][] grid = new Hexagon[gridSize][gridSize];

		Random generator = new Random();
		
		int countryCount = 1; // Used for ascii map
		
		List<Country> countries = new Vector<Country>();
		
		// Drop initial country hexagons
		for(int i = 0; i < totalCountries; i++)
		{
			int x = generator.nextInt(gridSize);
			int y = generator.nextInt(gridSize);
			
			if(grid[x][y] == null)
			{
				grid[x][y] = new Hexagon(x, y);
				Country country = new Country(3, new Vector<Hexagon>(), countryCount++); // 3 dice, empty hexagon vector, country ID
				grid[x][y].country = country; // add the country to the hexagon
				country.hexagons.add(grid[x][y]); // add the hexagon to the country's hexagon list
				countries.add(country); // add the country to the list of countries
			}
			else
			{
				i--;
			}
		}
		
		for(int q = 0; q < 3; q++)
		{
			for(int p = 0; p < totalCountries; p++)
			{
				
			}
		}
		
		for(int y = 0; y < gridSize; y++)
		{
			if(y % 2 == 1)
			{
				System.out.print(" ");
			}
			for(int x = 0; x < gridSize; x++)
			{
				int val = 0;
				if(grid[x][y] != null && grid[x][y].country != null)
				{
					val = grid[x][y].country.countID;
				}
				
				System.out.print(val + " ");
			}
			System.out.println();
		}

		return new GameState(countries);
	}

	protected List<Hexagon> neighbouringHexagons(Hexagon[][] grid, int x, int y)
	{
		Vector<Hexagon> neighbours = new Vector<Hexagon>();
		
		if(x > 0)
		{
			neighbours.add(grid[x-1][y]);
		}

		if(x < grid.length)
		{
			neighbours.add(grid[x+1][y]);
		}

		if(y > 0)
		{
			neighbours.add(grid[x][y-1]);
			if(y % 2 == 0)
			{
				neighbours.add(grid[x+1][y-1]);
			}
			else
			{
				neighbours.add(grid[x-1][y-1]);
			}
		}

		if(y < grid.length)
		{
			neighbours.add(grid[x][y]);
			if(y % 2 == 0)
			{
				neighbours.add(grid[x+1][y+1]);
			}
			else
			{
				neighbours.add(grid[x+1][y+1]);				
			}
		}
		
		return neighbours;
	}
}