import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

class Country
{
	public int player;

	public Vector<Country> neighbours;

	public Country(int player)
	{
		this.player = player;
	}

	static public void main(String[] args)
	{
		Vector<Country> countries = situationB();
		
		System.out.println(countLargestCluster(countries, 2));
	}

	static public int countLargestCluster(Vector<Country> countries, int player)
	{
		int largestClusterSize = 0;
		Set<Country> countedCountries = new HashSet<Country>();

		for (Country country : countries)
		{
			if (country.player != player)
				continue;

			if (countedCountries.contains(country))
				continue;

			int clusterSize = countCluster(country, countedCountries);

			if (clusterSize > largestClusterSize)
				largestClusterSize = clusterSize;
		}

		return largestClusterSize;
	}

	static public int countCluster(Country country, Set<Country> countedCountries)
	{
		int count = 1;

		countedCountries.add(country);

		for (Country neighbour : country.neighbours)
			if (neighbour.player == country.player)
				if (!countedCountries.contains(neighbour))
					count += countCluster(neighbour, countedCountries);

		return count;
	}

	static public Vector<Country> situationA()
	{
		Vector<Country> countries = new Vector<Country>();

		Country a = new Country(1);
		Country b = new Country(1);
		Country c = new Country(1);
		Country d = new Country(1);

		Country e = new Country(2);
		Country f = new Country(2);

		a.neighbours = new Vector<Country>();
		a.neighbours.add(b);
		a.neighbours.add(e);
		a.neighbours.add(f);

		b.neighbours = new Vector<Country>();
		b.neighbours.add(a);
		b.neighbours.add(c);
		b.neighbours.add(d);

		c.neighbours = new Vector<Country>();
		c.neighbours.add(b);

		d.neighbours = new Vector<Country>();
		d.neighbours.add(b);

		e.neighbours = new Vector<Country>();
		e.neighbours.add(a);
		e.neighbours.add(f);

		f.neighbours = new Vector<Country>();
		f.neighbours.add(a);
		f.neighbours.add(e);

		countries.add(a);
		countries.add(b);
		countries.add(c);
		countries.add(d);
		countries.add(e);
		countries.add(f);

		return countries;
	}

	static public Vector<Country> situationB()
	{
		Vector<Country> countries = new Vector<Country>();

		Country a = new Country(1);
		Country b = new Country(1);
		Country c = new Country(1);
		Country d = new Country(1);
		Country e = new Country(2);
		Country f = new Country(2);

		a.neighbours = new Vector<Country>();
		a.neighbours.add(e);
		a.neighbours.add(f);

		b.neighbours = new Vector<Country>();
		b.neighbours.add(e);
		b.neighbours.add(c);
		b.neighbours.add(d);

		c.neighbours = new Vector<Country>();
		c.neighbours.add(b);

		d.neighbours = new Vector<Country>();
		d.neighbours.add(b);

		e.neighbours = new Vector<Country>();
		e.neighbours.add(a);
		e.neighbours.add(b);
		e.neighbours.add(f);

		f.neighbours = new Vector<Country>();
		f.neighbours.add(a);
		f.neighbours.add(e);

		countries.add(a);
		countries.add(b);
		countries.add(c);
		countries.add(d);
		countries.add(e);
		countries.add(f);

		return countries;
	}
}