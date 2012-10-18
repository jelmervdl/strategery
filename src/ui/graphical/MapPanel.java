package ui.graphical;

import game.Country;
import game.GameState;
import game.Player;
import map.Hexagon;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Polygon;

import java.util.Map;

import javax.swing.JPanel;

public class MapPanel extends JPanel
{
	private GameState state;

	private Map<Country,Color> highlights;

	public void setState(GameState state)
	{
		this.state = state;
		repaint();
	}

	public void setHighlights(Map<Country,Color> highlights)
	{
		this.highlights = highlights;
		repaint();
	}

	private Polygon buildHexagon(int px, int py)
	{
		double c = 8,
			   a = .5 * c,
			   b = Math.sin(45) * c,
			   x = px * 2*b + (py % 2) * b,
			   y = py * 3*a;

		Polygon p = new Polygon();
		p.addPoint((int) (x + 0),   (int) (y + a));
		p.addPoint((int) (x + b),   (int) (y + 0));
		p.addPoint((int) (x + 2*b), (int) (y + a));
		p.addPoint((int) (x + 2*b), (int) (y + a + c));
		p.addPoint((int) (x + b),   (int) (y + 2*c));
		p.addPoint((int) (x + 0),   (int) (y + a + c));

		return p;
	}

	private void paintCountry(Country country, Graphics g)
	{
		if (highlights != null && highlights.containsKey(country))
			g.setColor(highlights.get(country));
		else
			g.setColor(country.getPlayer().getColor());

		for (Hexagon hexagon : country.getHexagons())
		{
			Polygon polygon = buildHexagon(hexagon.x, hexagon.y);
			g.fillPolygon(polygon);
		}
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		if (state != null)
			for (Country country : state.getCountries())
				paintCountry(country, g);
	}
}
