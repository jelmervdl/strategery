package game;

import java.util.List;
import java.util.Vector;
import java.util.UUID;

import map.Hexagon;

public class Country
{
	private UUID id;

	public Player player;

	public Vector<Country> neighbours;

	public Vector<Hexagon> hexagons;

	public int dice;

	public int countID;

	// generate country without specified player		
	public Country(int dice, int countID)
	{
		this.id = UUID.randomUUID();

		this.neighbours = new Vector<Country>();
		
		this.hexagons = new Vector<Hexagon>();

		this.dice = dice;
		
		this.countID = countID;
	}

	// generate empty country		
	public Country(Player player, int dice)
	{
		this.id = UUID.randomUUID();

		this.player = player;

		this.neighbours = new Vector<Country>();

		this.hexagons = new Vector<Hexagon>();

		this.dice = dice;
	}

	public Country(Country other)
	{
		// Copy constructor!
		id = other.id;
		player = other.player;
		neighbours = new Vector<Country>(other.neighbours);
		this.hexagons = other.hexagons;
		dice = other.dice;
	}

	public Player getPlayer()
	{
		return player;
	}

	public List<Hexagon> getHexagons()
	{
		return hexagons;
	}

	public List<Country> getNeighbours()
	{
		return neighbours;
	}

	public int maximumDice()
	{
		return 7;
	}

	public List<Country> enemyNeighbours()
	{
		Vector<Country> enemies = new Vector<Country>();

		for (Country neighbour : neighbours)
			if (neighbour.player != player)
				enemies.add(neighbour);

		return enemies;
	}

	public boolean equals(Object other)
	{
		if (this == other)
			return true;

		if (!(other instanceof Country))
			return false;

		return id == ((Country) other).id;
	}

	public String toString()
	{
		return "[Country " + id + " player:" + player + " dice:" + dice + "]";
	}
}