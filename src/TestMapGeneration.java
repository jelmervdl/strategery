import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import game.GameState;
import game.Player;
import game.RandomPlayer;
import game.Country;

import map.MapGenerator;
import map.MapReader;
import map.MapWriter;

import ui.graphical.MapPanel;

public class TestMapGeneration extends JFrame
{
	private JFileChooser filePicker;

	private MapPanel mapPanel;

	private GameState state;

	public TestMapGeneration()
	{
		super();

		setTitle("Strategery");
		setSize(500, 500);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		mapPanel = new MapPanel();
		getContentPane().add(mapPanel);

		JMenuBar bar = new JMenuBar();
		setJMenuBar(bar);
		
		JMenu fileMenu = new JMenu("File");
		bar.add(fileMenu);
		
		JMenuItem saveItem = new JMenuItem("Save Map...");
		saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.META_MASK));
		fileMenu.add(saveItem);

		JMenuItem openItem = new JMenuItem("Open Map...");
		openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.META_MASK));
		fileMenu.add(openItem);
		
		JMenu mapMenu = new JMenu("Map");
		bar.add(mapMenu);
		
		JMenuItem generateItem = new JMenuItem("Generate new map");
		generateItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.META_MASK));
		mapMenu.add(generateItem);

		generateItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				generateMap();
			}
		});

		saveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveMap();
			}
		});

		openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadMap();
			}
		});

		filePicker = new JFileChooser("./maps");
		filePicker.setFileFilter(new FileNameExtensionFilter("Strategery maps", "map"));
	}

	private void generateMap()
	{
		MapGenerator generator = new MapGenerator(getPlayers());
		
		GameState gamestate = generator.generate(16, 3.5);

		setState(gamestate);
	}

	private void saveMap()
	{
		int response = filePicker.showSaveDialog(this);

		if (response == JFileChooser.APPROVE_OPTION)
		{
			try {
				writeState(filePicker.getSelectedFile());
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "alert", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void loadMap()
	{
		int response = filePicker.showOpenDialog(this);

		if (response == JFileChooser.APPROVE_OPTION)
		{
			try {
				loadState(filePicker.getSelectedFile());
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "alert", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void setState(GameState state)
	{
		this.state = state;
		mapPanel.setState(state);
	}

	private void loadState(File file) throws IOException
	{
		MapReader reader = new MapReader(getPlayers());
		List<Country> countries = reader.read(file);
		setState(new GameState(countries));
	}

	private void writeState(File file) throws IOException
	{
		MapWriter writer = new MapWriter();
		writer.write(state.getCountries(), file);
	}

	private List<Player> getPlayers()
	{
		// Generate some players
		List<Player> players = new Vector<Player>();
		for (int i = 0; i < 5; ++i)
			players.add(new RandomPlayer("Player " + i));

		return players;
	}

	static public void main(String[] args) throws Exception
	{
		System.setProperty("apple.laf.useScreenMenuBar", "true");
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		TestMapGeneration window = new TestMapGeneration();
		window.setVisible(true);
	}
}