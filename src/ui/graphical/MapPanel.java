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
		hexagonIndex = new HashMap<Coordinate,Hexagon>();

		for (Country country : state.getCountries())
			for (Hexagon hexagon : country.getHexagons())
				hexagonIndex.put(new Coordinate(hexagon.x, hexagon.y), hexagon);
	}

	private void drawHexagon(Graphics2D g, Hexagon h)
	{
		double c = 8,
			   a = .5 * c,
			   b = Math.sin(45) * c,
			   x = h.x * 2*b + (h.y % 2) * b,
			   y = h.y * 3*a;

		Country country = hexagonIndex.get(new Coordinate(h.x, h.y)).country;

		Polygon poly = new Polygon();
		poly.addPoint((int) (x + 0),   (int) (y + a));
		poly.addPoint((int) (x + b),   (int) (y + 0));
		poly.addPoint((int) (x + 2*b), (int) (y + a));
		poly.addPoint((int) (x + 2*b), (int) (y + a + c));
		poly.addPoint((int) (x + b),   (int) (y + 2*c));
		poly.addPoint((int) (x + 0),   (int) (y + a + c));

		GeneralPath path = new GeneralPath();
		path.moveTo((int) (x + 0),   (int) (y + a + c));
		path.lineTo((int) (x + 0),   (int) (y + a));
		path.lineTo((int) (x + b),   (int) (y + 0));
		path.lineTo((int) (x + 2*b), (int) (y + a));
		path.lineTo((int) (x + 2*b), (int) (y + a + c));
		path.lineTo((int) (x + b),   (int) (y + 2*c));
		path.lineTo((int) (x + 0),   (int) (y + a + c));
		// path.closePath();

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
