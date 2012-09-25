import java.util.Vector;
import java.util.UUID;

class Country
{
	private UUID id;

	public Player player;

	public Vector<Country> neighbours;

	public int dice;

	// generate empty country		
	public Country(Player player, int dice)
	{
		this.id = UUID.randomUUID();

		this.player = player;

		this.neighbours = new Vector<Country>();

		this.dice = dice;
	}

	public Country(Country other)
	{
		// Copy constructor!
		id = other.id;
		player = other.player;
		neighbours = new Vector<Country>(other.neighbours);
		dice = other.dice;
	}

	public int maximumDice()
	{
		return 7;
	}

	public Vector<Country> enemyNeighbours()
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