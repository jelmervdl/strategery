package ui.graphical;

import game.Country;
import game.GameState;
import game.Player;
import map.Hexagon;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.GeneralPath;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

public class MapPanel extends JPanel
{
	private GameState state;

	private Map<Coordinate,Hexagon> hexagonIndex;

	private Map<Country,Color> highlights;

	private class Coordinate
	{
		public int x;

		public int y;

		public Coordinate(int x, int y)
		{
			this.x = x;
			this.y = y;
		}

		public int hashCode()
		{
			// This is probably unique enough for my purposes.
			return 512 * x + y;
		}

		public boolean equals(Object other)
		{
			if (other == this)
				return true;

			if (other == null || other.getClass() != getClass())
				return false;

			return x == ((Coordinate) other).x
				&& y == ((Coordinate) other).y;
		}

		/*
			# _ 1 2
			#  3 0 4
			# _ 5 6 7
			#  _ 8 9

			0: 1 1 // y % 2 == 1
			1: 1 0 
			2: 2 0
			3: 0 1
			4: 2 1
			5: 1 2
			6: 2 2 // y % 2 == 0
			7: 3 2
			8: 1 3
			9: 2 3
		*/

		public Coordinate left()
		{
			return new Coordinate(x - 1, y);
		}

		public Coordinate right()
		{
			return new Coordinate(x + 1, y);
		}

		public Coordinate topLeft()
		{
			return y % 2 == 0
				? new Coordinate(x - 1, y - 1)
				: new Coordinate(x    , y - 1);
		}

		public Coordinate topRight()
		{
			return y % 2 == 0
				? new Coordinate(x    , y - 1)
				: new Coordinate(x + 1, y - 1);
		}

		public Coordinate bottomLeft()
		{
			return y % 2 == 0
				? new Coordinate(x - 1, y + 1)
				: new Coordinate(x    , y + 1);
		}

		public Coordinate bottomRight()
		{
			return y % 2 == 0
				? new Coordinate(x    , y + 1)
				: new Coordinate(x + 1, y + 1);
		}
	}

	private class Side extends Coordinate
	{
		public Coordinate neighbour;

		public Side(Coordinate neighbour, int x, int y)
		{
			super(x, y);
			this.neighbour = neighbour;
		}
	}

	public MapPanel()
	{
		super();

		highlights = new HashMap<Country, Color>();
	}

	public void setState(GameState state)
	{
		this.state = state;
		reindex();
		repaint();
	}

	public void setHighlights(Map<Country,Color> highlights)
	{
		this.highlights = highlights;
		repaint();
	}

	private void reindex()
	{
		Map<Coordinate,Hexagon> index = new HashMap<Coordinate,Hexagon>();

		for (Country country : state.getCountries())
			for (Hexagon hexagon : country.getHexagons())
				index.put(new Coordinate(hexagon.x, hexagon.y), hexagon);

		hexagonIndex = index;
	}

	private boolean isSameCountry(Coordinate p, Country country)
	{
		return hexagonIndex.containsKey(p)
			? hexagonIndex.get(p).country == country
			: false;
	}

	private void drawHexagon(Graphics2D g, Hexagon h)
	{
		final Coordinate p = new Coordinate(h.x, h.y);

		final Country country = hexagonIndex.get(p).country;

		final double c = 8,
			   a = .5 * c,
			   b = Math.sin(45) * c,
			   x = p.x * 2*b + (p.y % 2) * b,
			   y = p.y * 3*a;

		final Side[] sides = {
			new Side(p.left(), 		(int) (x + 0),   (int) (y + a)), 		// left
			new Side(p.topLeft(), 	(int) (x + b),   (int) (y + 0)),		// top-left
			new Side(p.topRight(), 	(int) (x + 2*b), (int) (y + a)),		// top-right
			new Side(p.right(), 		(int) (x + 2*b), (int) (y + a + c)),// right
			new Side(p.bottomRight(),(int) (x + b),   (int) (y + 2 * c)),	// bottom-right
			new Side(p.bottomLeft(),	(int) (x + 0),   (int) (y + a + c))	// bottom-left
		};

		// The body
		Polygon poly = new Polygon();
		for (Side side : sides)
			poly.addPoint(side.x, side.y);
		
		// The border
		GeneralPath path = new GeneralPath();
		path.moveTo((int) (x + 0),   (int) (y + a + c));
		for (Side side : sides)
			if (!isSameCountry(side.neighbour, country))
				path.lineTo(side.x, side.y);
			else
				path.moveTo(side.x, side.y);
		
		if (highlights != null && highlights.containsKey(country))
			g.setColor(highlights.get(country));
		else
			g.setColor(country.getPlayer().getColor());

		g.fillPolygon(poly);

		g.setColor(Color.BLACK);
		g.draw(path);
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		if (state == null)
			return;

		for (Hexagon hexagon : hexagonIndex.values())
			drawHexagon((Graphics2D) g, hexagon);
	}
}
