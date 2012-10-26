package ui.graphical;

import game.Country;
import game.GameState;
import game.Player;
import map.Hexagon;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.geom.GeneralPath;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

public class MapPanel extends JPanel
{
	private GameState state;

	private Map<Coordinate,Hexagon> hexagonIndex;

	private Set<Country> highlights;

	private HashSet<ActionListener> eventListeners;

	private double c;

	private double a;

	private double b;

	private int mapWidth;

	private int mapHeight;

	public interface ActionListener
	{
		public void actionPerformed(ActionEvent event);
	}

	public class ActionEvent
	{
		private Country country;

		private MapPanel source;

		public ActionEvent(MapPanel source, Country country)
		{
			this.source = source;
			this.country = country;
		}

		public Country getCountry()
		{
			return country;
		}

		public MapPanel getSource()
		{
			return source;
		}
	}

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

		highlights = new HashSet<Country>();

		eventListeners = new HashSet<ActionListener>();

		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e)
			{
				Country country = getCountryAt(e.getPoint());
				publishActionEvent(new MapPanel.ActionEvent((MapPanel) e.getSource(), country));
			}
		});

		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e)
			{
				calculateScale();
				repaint();
			}
		});
	}
	public void addActionListener(ActionListener listener)
	{
		eventListeners.add(listener);
	}

	public void removeActionListener(ActionListener listener)
	{
		eventListeners.remove(listener);
	}

	private void publishActionEvent(ActionEvent event)
	{
		for (ActionListener listener : eventListeners)
			listener.actionPerformed(event);
	}

	public void setState(GameState state)
	{
		this.state = state;
		reindex();
		calculateScale();
		repaint();
	}

	public void setHighlights(Set<Country> highlights)
	{
		// I hate nullpointers, therefore I ignore them.
		if (highlights == null)
			highlights = new HashSet<Country>();

		this.highlights = highlights;

		repaint();
	}

	private void calculateScale()
	{
		if (state == null)
			return;

		// c calculated using width
		// 2 * b = 1 hexagon
		// 2 * b * mapWidth == getWidth()
		// 2 * Math.sin(45) * c * mapWidth  == getWidth()
		// c = getWidth() / (2 * Math.sin(45) * mapWidth)

		// c calculated using height
		// 0.5 * c + mapHeight * 1.5 * c = getHeight()
		// c + mapHeight * 3 * c = getHeight() * 2
		// (3 * mapHeight + 1) * c = getHeight * 2
		// c = (getHeight * 2) / (3 * mapHeight + 1)

		c = Math.min(
			(double) getWidth() / (2.0 * Math.sin(45) * (mapWidth + .5)), // c calculated using width
			(double) getHeight() / (1.5 * mapHeight + 0.5) // c calculated using height
		);

		a = .5 * c;
		b = Math.sin(45) * c;
	}

	private void reindex()
	{
		Map<Coordinate,Hexagon> index = new HashMap<Coordinate,Hexagon>();
		mapWidth = 0;
		mapHeight = 0;

		for (Country country : state.getCountries())
			for (Hexagon hexagon : country.getHexagons())
			{
				index.put(new Coordinate(hexagon.x, hexagon.y), hexagon);

				// +1 because offset 0 == size 1.
				if (hexagon.x + 1 > mapWidth)
					mapWidth = hexagon.x + 1;

				if (hexagon.y + 1 > mapHeight)
					mapHeight = hexagon.y + 1;
			}

		hexagonIndex = index;
	}

	private Country getCountryAt(Coordinate p)
	{
		return hexagonIndex.containsKey(p)
			? hexagonIndex.get(p).country
			: null;
	}

	private Country getCountryAt(Point p)
	{
		if (state == null)
			return null;

		double py = (double) p.y / (3 * a),
			   px = (p.x + (py % 2) * b) / (2 * b);

		Coordinate coordinate = new Coordinate((int) (px - .5), (int) (py - .5));

		return getCountryAt(coordinate);
	}

	private boolean isSameCountry(Coordinate p, Country country)
	{
		Country found = getCountryAt(p);

		return found != null && found.equals(country);
	}

	private void drawHexagon(Graphics2D g, Country country, Hexagon h, Color color)
	{
		Coordinate p = new Coordinate(h.x, h.y);

		double x = p.x * 2*b + (p.y % 2) * b,
			   y = p.y * 3*a;

		Side[] sides = {
			new Side(p.left(), 			(int) (x + 0),   (int) (y + a)),
			new Side(p.topLeft(), 		(int) (x + b),   (int) (y + 0)),
			new Side(p.topRight(), 		(int) (x + 2*b), (int) (y + a)),
			new Side(p.right(), 		(int) (x + 2*b), (int) (y + a + c)),
			new Side(p.bottomRight(),	(int) (x + b),   (int) (y + 2 * c)),
			new Side(p.bottomLeft(),	(int) (x + 0),   (int) (y + a + c))
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
		
		g.setColor(color);
		g.fillPolygon(poly);

		g.setColor(Color.BLACK);
		g.draw(path);
	}

	private void drawDiceOnHexagon(Graphics2D g, int dice, Hexagon h)
	{
		double x = h.x * 2*b + (h.y % 2) * b,
			   y = h.y * 3*a;

		FontMetrics fm = getFontMetrics(getFont());
		int textWidth = fm.stringWidth(Integer.toString(dice)),
			textHeight = fm.getHeight();

		g.drawString(Integer.toString(dice), (float) (x + b - .5 * textWidth), (float) (y + c + 1f/3 * textHeight));
	}

	private void clearCanvas(Graphics g)
	{
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
	}

	private Hexagon findCentermostHexagon(List<Hexagon> hexagons)
	{
		int sx = 0, sy = 0;

		for (Hexagon hexagon : hexagons)
		{
			sx += hexagon.x;
			sy += hexagon.y;
		}

		// Average dx,dy, or, the center.
		double ax = (double) sx / hexagons.size();
		double ay = (double) sy / hexagons.size();

		double smallestDistance = Double.POSITIVE_INFINITY;
		Hexagon closestHexagon = null;

		for (Hexagon h : hexagons)
		{
			double distance = (ax - h.x) * (ax - h.x) + (ay - h.y) * (ay - h.y);
			if (distance < smallestDistance)
			{
				smallestDistance = distance;
				closestHexagon = h;
			}
		}

		return closestHexagon;
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		Stroke defaultStroke = g2d.getStroke();
		Stroke fatStroke = new BasicStroke(2);

		if (state == null)
			return;

		for (Country country : state.getCountries())
		{
			Color fillColor = country.getPlayer().getColor();

			// If there are highlighted countries, and I'm not one of them, dimm my background.
			if (highlights.size() != 0 && !highlights.contains(country))
				fillColor = new Color(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), 96);

			// Use fat stroke for highlighted countries.
			g2d.setStroke(highlights.contains(country) ? fatStroke : defaultStroke);
			
			Hexagon center = findCentermostHexagon(country.getHexagons());

			for (Hexagon hexagon : country.getHexagons())
				drawHexagon(g2d, country, hexagon, hexagon.equals(center) ? fillColor.darker() : fillColor);

			drawDiceOnHexagon(g2d, country.getDice(), center);
		}
	}
}
