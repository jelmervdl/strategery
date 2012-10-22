package game;

import java.util.List;
import java.util.Random;
import java.awt.Color;

public abstract class PlayerAdapter implements Player
{
	private String name;

	private Color color;

	public PlayerAdapter(String name)
	{
		this.name = name;

		this.color = randomColor();
	}

	public Color getColor()
	{
		return color;
	}
	
	public String toString()
	{
		return "[Player " + name + "]";
	}

	protected Color randomColor()
	{
		Random random = new Random();

		return new Color(
			random.nextFloat(),
			random.nextFloat(),
			random.nextFloat());
	}
}
