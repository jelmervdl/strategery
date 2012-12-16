import csv.CSVWriter;

import descriptors.Descriptor;

import java.util.*;
import java.io.File;

import game.*;

import map.MapGenerator;

import td.GameStateEncoder;
import td.TDLearning;
import td.TDPlayer;

import util.Configuration;

public class TestDescriptors 
{
	static private class Experiment extends GameEventAdapter
	{
		Configuration config;

		CSVWriter writer;

		TDLearning brain;

		TDPlayer tdPlayer;

		List<Player> players;

		MapGenerator generator;

		GameStateEncoder encoder;

		int n;

		public Experiment(Configuration config, CSVWriter writer)
		{
			this.config = config;

			this.writer = writer;

			brain = new TDLearning(config.getSection("network"));

			tdPlayer = new TDPlayer("TD", brain, config.getSection("player"));

			players = new Vector<Player>();
			players.add(tdPlayer);
			players.add(new RandomPlayer("Random"));
			players.add(new SimplePlayer("Simple"));
		
			generator = new MapGenerator(players);

			encoder = GameStateEncoder.buildDefaultEncoder();
		}

		public void onStateChange(GameState state)
		{
			writer.write(n++);

			for (double value : encoder.encode(state, tdPlayer))
				writer.write(value);

			writer.endLine();
		}

		public void call()
		{
			n = 0;

			writer.write("step");

			// Print descriptor names
			for (Descriptor descriptor : encoder.getDescriptors())
				writer.write(descriptor.getClass().getSimpleName());

			writer.endLine();

			GameState state = generator.generate(4, 2.5);
				
			Game game = new Game(players, state);
			game.addEventListener(this);

			game.run();
		}
	}

	static public void main(String[] args) throws Exception
	{
		Configuration config = new Configuration();
		
		CSVWriter output = new CSVWriter(System.out);

		Experiment experiment = new Experiment(config, output);

		experiment.call();
	}
}
