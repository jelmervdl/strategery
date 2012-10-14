package graph;

import java.util.*;

class Pair
{
	public double x;

	public double y;

	public Pair(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	public Pair add(Pair other)
	{
		return new Pair(x + other.x, y + other.y);
	}

	public Pair subtract(Pair other)
	{
		return new Pair(x - other.x, y - other.y);
	}

	public Pair multiply(double times)
	{
		return new Pair(x * times, y * times);
	}

	public double squared()
	{
		return this.x * this.x + this.y * this.y;
	}

	public String toString()
	{
		return "(" + x + ", " + y + ")";
	}

	static public Pair random()
	{
		Random random = new Random();

		return new Pair(
			random.nextDouble(),
			random.nextDouble());
	}
}