import csv.CSVWriter;

import descriptors.Descriptor;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.io.File;

import game.*;

import map.MapGenerator;

import td.GameStateEncoder;
import td.TDLearning;
import td.TDPlayer;

import util.Configuration;

public class SampleDescriptors 
{
	static private class Experiment extends GameEventAdapter implements Callable<Vector<double[]>>
	{
		Configuration config;

		TDLearning brain;

		TDPlayer tdPlayer;

		List<Player> players;

		MapGenerator generator;

		GameStateEncoder encoder;

		Vector<double[]> samples;

		public Experiment(Configuration config, GameStateEncoder encoder)
		{
			this.config = config;

			brain = new TDLearning(config.getSection("network"));

			tdPlayer = new TDPlayer("TD", brain, config.getSection("player"));

			players = new Vector<Player>();
			players.add(tdPlayer);
			players.add(new RandomPlayer("Random"));
			players.add(new SimplePlayer("Simple"));
		
			generator = new MapGenerator(players);

			this.encoder = encoder;
		}

		public void onStateChange(GameState state)
		{
			samples.add(encoder.encode(state, tdPlayer));
		}

		public Vector<double[]> call()
		{
			samples = new Vector<double[]>();

			GameState state = generator.generate(4, 2.5);
				
			Game game = new Game(players, state);
			game.addEventListener(this);

			game.run();

			return samples;
		}
	}

	static public void main(String[] args) throws Exception
	{
		int nGames = 500; // How many games should be averaged
		int nBins = 50; // How many samples should there be in the output

		Configuration config = new Configuration();
		
		GameStateEncoder encoder = GameStateEncoder.buildDefaultEncoder();

		ExecutorService queue = Executors.newFixedThreadPool(8);

		Vector<Future<Vector<double[]>>> results = new Vector<Future<Vector<double[]>>>();

		for (int i = 0; i < nGames; ++i)
			results.add(queue.submit(new Experiment(config, encoder)));
		
		queue.shutdown();

		while (queue.awaitTermination(1, TimeUnit.SECONDS) == false)
			printStatus(results);

		Vector<double[]> averages = sampleResults(results, nBins);

		CSVWriter writer = new CSVWriter(System.out);

		for (Descriptor descriptor : encoder.getDescriptors())
			writer.write(descriptor.getClass().getSimpleName());

		writer.endLine();

		for (int b = 0; b < nBins; ++b)
		{
			for (int d = 0; d < encoder.getDescriptors().size(); ++d)
				writer.write(averages.get(d)[b]);

			writer.endLine();
		}
	}

	static private void printStatus(Vector<Future<Vector<double[]>>> results)
	{
		int finishedTasks = 0;

		for (Future task : results)
			if (task.isDone())
				finishedTasks++;

		System.err.println("Finished " + finishedTasks + " of " + results.size() + " tasks");
	}

	static private Vector<double[]> sampleResults(Vector<Future<Vector<double[]>>> results, int bins) throws Exception
	{
		int nDescriptors = results.get(0).get().get(0).length;
		System.out.println("Printing for " + nDescriptors + " descriptors");

		Vector<double[]> descriptors = new Vector<double[]>(nDescriptors);

		for (int d = 0; d < nDescriptors; ++d)
		{
			double[] averages = new double[bins];

			// for each experiment
			for (Future<Vector<double[]>> result : results)	
			{
				Vector<double[]> samples = result.get();
				
				int samplesPerBin = samples.size() / bins;

				// for each bin
				for (int b = 0; b < bins; ++b)
				{
					double sum = 0;
					int counted = 0;

					// for each sample that should go in this bin
					for (int s = 0; s < samplesPerBin && b * samplesPerBin + s < samples.size(); ++s)
					{
						sum += samples.get(b * samplesPerBin + s)[d];
						counted += 1;
					}

					averages[b] = sum / counted;
				}
			}

			descriptors.add(averages);
		}

		return descriptors;
	}
}
