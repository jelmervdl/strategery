import java.io.File;
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

	static public void main(String[] args) throws Exception
	{
		NeuralNetwork network = new NeuralNetwork(new int[]{2,2,1});

		List<Sample> samples = new Vector<Sample>();
		samples.add(new Sample(new double[]{1.0, 1.0}, new double[]{0.0}));
		samples.add(new Sample(new double[]{1.0, 0.0}, new double[]{1.0}));
		samples.add(new Sample(new double[]{0.0, 1.0}, new double[]{1.0}));
		samples.add(new Sample(new double[]{0.0, 0.0}, new double[]{0.0}));

		File weightsFile = args.length > 0 ? new File(args[0]) : null;

		if (weightsFile != null && weightsFile.exists())
		{
			network.readWeights(weightsFile);
			System.out.println("Weigths read from " + weightsFile.getName());
		}
		else
		{
			train(network, samples);
			System.out.println("Weights trained");
		}

		if (weightsFile != null && !weightsFile.exists())
		{
			network.writeWeights(weightsFile);
			System.out.println("Weigths written to " + weightsFile.getName());
		}

		test(network, samples);
	}

	static private void train(NeuralNetwork network, List<Sample> samples)
	{
		for (int epoch = 0; epoch < 1000; ++epoch)
		{
			Collections.shuffle(samples);

			for (Sample sample : samples)
			{
				network.input().setValues(sample.input);

				network.forwardPropagate();

				network.backPropagate(sample.output, 0.1);
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