package game;

import java.util.Random;
import java.util.List;
import java.util.Vector;

public class GameState
{
	List<Country> countries;

	public GameState()
	{
		this.countries = new Vector<Country>();
	}

	public GameState(List<Country> countries)
	{
		this.countries = countries;
	}

	public GameState(GameState other)
	{
		countries = new Vector<Country>();

		// Copy all the countries
		for (Country country : other.countries)
			countries.add(new Country(country));

		// Update neighbours to direct to the countries in the current state
		for (Country country : countries)
			for (int i = 0; i < country.neighbours.size(); ++i)
				country.neighbours.set(i, getCountry(country.neighbours.get(i)));
	}

	// nCountries: Number of countries per player
	// players: Participating players
	// nBorders: Average number of borders required per country
	public GameState(int nCountries, List<Player> players, double nBorders)
	{
		int nPlayers = players.size();
		int totalCountries = nCountries * nPlayers;
		int gridSize = (int)(Math.sqrt(totalCountries) * 4);
		Hexagon[][] grid = new Hexagon[gridSize][gridSize];

		Random generator = new Random();
		
		int countryCount = 1; // Used for ascii map
		
		countries = new Vector<Country>();
		
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
	}

	public List<Hexagon> neighbouringHexagons(Hexagon[][] grid, int x, int y)
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

	public GameState apply(Move move, Boolean Expected)
	{
		GameState state = new GameState(this);

        int attackingEyes;
        int defendingEyes;
        if(Expected)
        {
            attackingEyes = move.attackingCountry.dice;
		    defendingEyes = move.defendingCountry.dice;
            
        }
        else
        {
		    // fight battle (if there is one)
		    attackingEyes = rollDice(move.attackingCountry.dice);
		    defendingEyes = rollDice(move.defendingCountry.dice);
        }
		// Attacker wins
		if (attackingEyes > defendingEyes)
		{
			// Take the country!
			state.getCountry(move.defendingCountry).player = move.attackingCountry.player;

			// Reset attacking country dice to 1 (as the army has moved to the defending country)
			state.getCountry(move.attackingCountry).dice = 1;

			// Assign remaining dice to country
			state.getCountry(move.defendingCountry).dice = remainingDice(attackingEyes - defendingEyes);

			System.out.println("Attacker wins!");
		}

		// It's a draw
		else if (attackingEyes == defendingEyes)
		{
			state.getCountry(move.attackingCountry).dice = 1;

			state.getCountry(move.defendingCountry).dice = 1;

			System.out.println("Draw!");
		}

		// Attacker loses
		else
		{
			state.getCountry(move.attackingCountry).dice = 1;

			state.getCountry(move.defendingCountry).dice = remainingDice(defendingEyes - attackingEyes);

			System.out.println("Defender wins!");
		}

		return state;
	}

	public List<Move> generatePossibleMoves(Player player)
	{
		List<Move> moves = new Vector<Move>();

		for (Country country : countries)
		{
			if (country.player != player)
			{
				// System.out.println("Not same player");
				continue;
			}

			if (country.dice <= 1)
			{
				// System.out.println("Not enough dice");
				continue;
			}

			List<Country> enemyNeighbours = country.enemyNeighbours();

			if (enemyNeighbours.size() == 0)
			{
				// System.out.println("Not enough enemy neighbours");
				continue;
			}

			for (Country enemyCountry : enemyNeighbours)
				moves.add(new Move(country, enemyCountry));
		}

		return moves; 
	}

	public Country getCountry(Country country)
	{
		return countries.get(countries.indexOf(country));
	}

	public List<Country> getCountries()
	{
		return countries;
	}

	public List<Country> getCountries(Player player)
	{
		List<Country> playerCountries = new Vector<Country>();

		for (Country country : countries)
			if (country.player == player)
				playerCountries.add(country);

		return playerCountries;
	}

	public String toString()
	{
		String out = "[GameState countries:\n";

		for (Country country : countries)
			out += "\t" + country + "\n";

		out += "]";

		return out;
	}

	private int rollDice(int dice)
	{
		Random random = new Random();

		int eyes = 0;

		for (int i = 0; i < dice; ++i)
			eyes += 1 + random.nextInt(6);

		return eyes;
	}

	private int remainingDice(int eyes)
	{
		return (int) Math.ceil(eyes / 6.0);
	}
}
