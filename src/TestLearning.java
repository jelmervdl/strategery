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
import util.Instrument;

public class TestLearning
{
	static private class Experiment extends GameEventAdapter
	{
		Configuration config;

		TDLearning brain;

		List<Player> players;

		MapGenerator generator;

		int samples = 500000;

		public Experiment(Configuration config)
		{
			this.config = config;

			brain = new TDLearning(config.getSection("network"));
			brain.getError().reset(samples + 1000);

			players = new Vector<Player>();
			players.add(new TDPlayer("TD", brain, config.getSection("player")));
			players.add(new TDPlayer("TD 2", brain, config.getSection("player")));
		
			generator = new MapGenerator(players);
		}

		public double[] call()
		{
			while (brain.getError().getIndex() < samples)
			{
				GameState state = generator.generate(4, 2.5);
					
				Game game = new Game(players, state);
				game.addEventListener(this);

				game.run();
			}

			return brain.getError().toArray();
		}
	}

	static public void main(String[] args) throws Exception
	{
		Configuration config = new Configuration();

		if (args.length > 0)
		{
			File configFile = new File(args[0]);
			config.read(configFile);
		}
		
		CSVWriter output = new CSVWriter(System.out);

		Experiment experiment = new Experiment(config);

		double[] error = experiment.call();

		output.write("mean");
		output.write("min");
		output.write("max");
		output.write("var");
		output.endLine();

		for (Sample sample : resample(error, 50))
		{
			output.write(sample.mean());
			output.write(sample.min());
			output.write(sample.max());
			output.write(sample.variance());
			output.endLine();
		}
	}

	static class Sample
	{
		private Vector<Double> data;

		public double min()
		{
			if (count() == 0)
				return 0;

			double min = data.get(0);

			for (Double value : data)
				if (value < min)
					min = value;

			return min;
		}

		public double max()
		{
			if (count() == 0)
				return 0;
			
			double max = data.get(0);

			for (Double value : data)
				if (value > max)
					max = value;

			return max;
		}

		public double mean()
		{
			if (count() == 0)
				return 0;
			
			return sum() / count();
		}

		public double sum()
		{
			double sum = 0;

			for (Double value : data)
				sum += value;

			return sum;
		}

		public int count()
		{
			return data.size();
		}

		public double variance()
		{
			double diff = 0;
			double mean = mean();

			for (double value : data)
				diff += (value - mean) * (value - mean);

			return Math.sqrt(diff / count());
		}

		public Sample()
		{
			this.data = new Vector<Double>();
		}

		public void add(double value)
		{
			this.data.add(value);
		}
	}

	static private Sample[] resample(double[] samples, int numberOfNewSamples)
	{
		// samples.length = 115;
		// numberOfNewSamples = 20;

		Sample[] resampled = new Sample[numberOfNewSamples];
		for (int s = 0; s < resampled.length; ++s)
			resampled[s] = new Sample();

		int samplesPerNewSample = (int) Math.ceil((double) samples.length / numberOfNewSamples);
		
		for (int s = 0; s < samples.length; ++s)
			resampled[s / samplesPerNewSample].add(samples[s]);
		
		return resampled;
	}
}
