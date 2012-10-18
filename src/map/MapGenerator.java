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
		for(int x = 0; x < gridSize; x++)
		{
			for(int y = 0; y < gridSize; y++)
			{
				grid[x][y] = new Hexagon(x, y);
			}
		}

		Random generator = new Random();
		
		int countryCount = 1; // Used for ascii map
		int filledHexagons = 0;
		
		List<Country> countries = new Vector<Country>();
		
		// Drop initial country hexagons
		for(int i = 0; i < totalCountries; i++)
		{
			int x = generator.nextInt(gridSize);
			int y = generator.nextInt(gridSize);
			
			if(grid[x][y].country == null)
			{
				Country country = new Country(3, countryCount++);
				grid[x][y].country = country;
				country.hexagons.add(grid[x][y]);
				countries.add(country);
				filledHexagons++;
			}
			else
			{
				i--;
			}
		}
		
		// Expanding landmass
		while((AverageNeighbours(countries) < nBorders || Continent(countries.get(0)).size() < countries.size()) && (filledHexagons < gridSize*gridSize))
		{
			// System.out.println(AverageNeighbours(countries));
			for(int i = 0; i < totalCountries; i++)
			{
				Vector<Hexagon> hexNeighbours = new Vector<Hexagon>();
				
				// Generate all neighbouring hexagons
				for(int j = 0; j < countries.get(i).hexagons.size(); j++)
				{
					Hexagon hex = countries.get(i).hexagons.get(j);
					hexNeighbours.addAll(neighbouringHexagons(grid, hex.x, hex.y, true));
				}
				
				// Pick random neighbouring hexagon and expand country
				if(hexNeighbours.size() > 0)
				{
					Hexagon newHex = hexNeighbours.get( generator.nextInt(hexNeighbours.size()) );
					countries.get(i).hexagons.add(newHex);
					newHex.country = countries.get(i);
					filledHexagons++;
				}
			}
			UpdateNeighbours(countries, grid);
		}
		
		DistributeCountries(countries);
		
		// PrintNeighbours(countries);
		
		// Printing grid
		// for(int y = 0; y < gridSize; y++)
		// {
		// 	if(y % 2 == 1)
		// 	{
		// 		System.out.print(" ");
		// 	}
		// 	for(int x = 0; x < gridSize; x++)
		// 	{
		// 		char ch = ' ';
		// 		if(grid[x][y] != null && grid[x][y].country != null)
		// 		{
		// 			ch = (char)(grid[x][y].country.countID + 64);
		// 		}
				
		// 		System.out.print(ch + " ");
		// 	}
		// 	System.out.println();
		// }
		// System.out.println();

		return new GameState(countries);
	}

	protected List<Hexagon> neighbouringHexagons(Hexagon[][] grid, int x, int y, boolean isEmpty)
	{
		Vector<Hexagon> neighbours = new Vector<Hexagon>();
		if(x > 0)
		{
			neighbours.add(grid[x-1][y]);
		}

		if(x+1 < grid.length)
		{
			neighbours.add(grid[x+1][y]);
		}

		if(y > 0)
		{
			neighbours.add(grid[x][y-1]);
			if(y % 2 == 1)
			{
				if(x+1 < grid.length)
				{
					neighbours.add(grid[x+1][y-1]);
				}
			}
			else
			{
				if(x > 0)
				{
					neighbours.add(grid[x-1][y-1]);
				}
			}
		}
		if(y+1 < grid.length)
		{
			neighbours.add(grid[x][y+1]);
			if(y % 2 == 1)
			{
				if(x+1 < grid.length)
				{
					neighbours.add(grid[x+1][y+1]);
				}
			}
			else
			{
				if(x > 0)
				{
					neighbours.add(grid[x-1][y+1]);
				}
			}
		}
		
		// Get rid of all the empty OR all the filled hexagons, depending on need
		for(int i = neighbours.size() - 1; i >= 0; i--)
		{
			if(isEmpty && neighbours.get(i).country != null)
			{
				neighbours.remove(i);
			}
			if(!isEmpty && neighbours.get(i).country == null)
			{
				neighbours.remove(i);
			}
		}
		
		return neighbours;
	}
	
	protected void UpdateNeighbours(List<Country> countries, Hexagon[][] grid)
	{
		for(int i = 0; i < countries.size(); i++)
		{
			Country country = countries.get(i);
			for(int j = 0; j < country.hexagons.size(); j++)
			{
				Vector<Hexagon> neighbours = (Vector<Hexagon>)(neighbouringHexagons(grid, country.hexagons.get(j).x, country.hexagons.get(j).y, false));
				for(int k = 0; k < neighbours.size(); k++)
				{
					if(!country.neighbours.contains(neighbours.get(k).country))
					{
						country.neighbours.add(neighbours.get(k).country);
					}
				}
			}
			country.neighbours.remove(country);
		}
	}
	
	protected float AverageNeighbours(List<Country> countries)
	{
		int total = 0;
		for(int i = 0; i < countries.size(); i++)
		{
			total += countries.get(i).neighbours.size();
		}
		return ((float)total / (float)(countries.size()));
	}
	
	protected void PrintNeighbours(List<Country> countries)
	{
		for(int i = 0; i < countries.size(); i++)
		{
			System.out.print((char)(countries.get(i).countID+64) + " owned by " + countries.get(i).player + ". Neighbours: ");
			Vector<Country> neighbours = countries.get(i).neighbours;
			for(int j = 0; j < neighbours.size(); j++)
			{
				System.out.print((char)(neighbours.get(j).countID + 64) + " ");
			}
			System.out.println();
		}
	}
	
	protected List<Country> Continent(Country country)
	{
		Vector<Country> continent = new Vector<Country>();
		continent.add(country);
		
		for(int i = 0; i < continent.size(); i++)
		{
			Vector<Country> neighbours = continent.get(i).neighbours;
			for(int j = 0; j < neighbours.size(); j++)
			{
				if(!continent.contains(neighbours.get(j)))
				{
					continent.add(neighbours.get(j));
				}
			}
		}
		
		return continent;
	}
	
	protected void DistributeCountries(List<Country> countries)
	{
		Vector<Country> copy = new Vector<Country>(countries);
		int neighbours = 1;
		int count = 0;
		while(!copy.isEmpty())
		{
			for(int i = copy.size() - 1; i >= 0 ; i--)
			{
				if (copy.get(i).neighbours.size() == neighbours)
				{
					copy.get(i).player = players.get(count++ % players.size());
					copy.remove(i);
				}
			}
			neighbours++;
		}
	}
}