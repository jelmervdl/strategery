import java.util.Vector;

class Country
{
	public Player player;

	public Vector<Country> neighbours;

	public int dice;

	public Country(Player player, int dice)
	{
		// generate empty country
		this.player = player;

		this.neighbours = new Vector<Country>();

		this.dice = dice;
	}

	public Country(Country other)
	{
		// Copy constructor!
		this.player = player;
		this.neighbours = new Vector<Country>(neighbours);
		this.dice = dice;
	}

	public Vector<Country> enemyNeighbours()
	{
		Vector<Country> enemies = new Vector<Country>();

		for (Country neighbour : neighbours)
			if (neighbour.player != player)
				enemies.add(neighbour);

		return enemies;
	}
}