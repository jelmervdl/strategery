package map;

import game.Country;

public class Hexagon
{
	public Country country; // country the hexagon belongs to

	public int x;
	
	public int y;
	
	public Hexagon(int x, int y)
	{
		this.x = x;
		this.y = y;
	}	
	
	public Hexagon(Country country, int x, int y)
	{
		this.country = country;
		this.x = x;
		this.y = y;
	}	
}
