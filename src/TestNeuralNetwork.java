import java.util.*;
import neuralnetwork.NeuralNetwork;

public class TestNeuralNetwork
{
	static private class Sample
	{
		public double[] input;

		public double[] output;

		public Sample(double[] input, double[] output)
		{
			this.input = input;
			this.output = output;
		}
	}

	static public void main(String[] args)
	{
		NeuralNetwork network = new NeuralNetwork(new int[]{2,2,1});

		List<Sample> samples = new Vector<Sample>();
		samples.add(new Sample(new double[]{1.0, 1.0}, new double[]{0.0}));
		samples.add(new Sample(new double[]{1.0, 0.0}, new double[]{1.0}));
		samples.add(new Sample(new double[]{0.0, 1.0}, new double[]{1.0}));
		samples.add(new Sample(new double[]{0.0, 0.0}, new double[]{0.0}));

		train(network, samples);

		test(network, samples);
	}

	static private void train(NeuralNetwork network, List<Sample> samples)
	{
		for (int epoch = 0; epoch < 10000; ++epoch)
		{
			Collections.shuffle(samples);

			for (Sample sample : samples)
			{
				network.input().setValues(sample.input);

				network.forwardPropagate();

				// System.out.println("Sample: " + sample.output[0]
				// 	+ "\tNetwork: " + network.output().output(0)
				// 	+ "\tDiff: " + (sample.output[0] - network.output().output(0)));

				network.backPropagate(sample.output, 0.01);
			}
		}
	}

	static private void test(NeuralNetwork network, List<Sample> samples)
	{
		double error = 0;

		for (Sample sample : samples)
		{
			network.input().setValues(sample.input);

			network.forwardPropagate();

			double diff = sample.output[0] - network.output().output(0);

			error += diff * diff;
		}

		System.out.println("Error: " + error);
	}
}