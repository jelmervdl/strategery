import java.util.Vector;

class Country
{
	public Player player;

	public Vector<Country> neighbours;

	public int dice;

	public Vector<Country> enemyNeighbours()
	{
		Vector<Country> enemies = new Vector<Country>();

		for (Country neighbour : neighbours)
			if (neighbour.player != player)
				enemies.add(neighbour);

		return enemies;
	}

	public Country clone()
	{
		Country country = new Country();
		country.player = player;
		country.neighbours = neighbours;
		country.dice = dice;

		return country;
	}
}