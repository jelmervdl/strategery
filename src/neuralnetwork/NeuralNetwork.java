package neuralnetwork;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

public class NeuralNetwork
{
	List<Layer> layers;

	List<double[][]> weights;

	public NeuralNetwork(int[] layerSizes)
	{
		layers = new Vector<Layer>();

		for (int i = 0; i < layerSizes.length; ++i)
			layers.add(new Layer(layerSizes[i], i == 0 ? new Linear() : new Exp()));

		weights = new Vector<double[][]>();

		for (int i = 0; i < layers.size() - 1; ++i)
			weights.add(new double[layers.get(i).size() + 1][layers.get(i + 1).size()]);

		randomizeWeights(0.5);
	}

	public Layer input()
	{
		return layers.get(0);
	}

	public Layer output()
	{
		return layers.get(layers.size() - 1);
	}

	public void readWeights(File file) throws IOException
	{
		Scanner in = new Scanner(file);

		for (int l = 0; l < layers.size() - 1; ++l)
		{
			double[][] w = weights.get(l);

			for (int i = 0; i < layers.get(l).size() + 1; ++i)
				for (int j = 0; j < layers.get(l + 1).size(); ++j)
					w[i][j] = in.nextDouble();
		}
	}

	public void writeWeights(File file) throws IOException
	{
		PrintWriter out = new PrintWriter(file);

		for (int l = 0; l < layers.size() - 1; ++l)
		{
			double[][] w = weights.get(l);

			for (int i = 0; i < layers.get(l).size() + 1; ++i)
				for (int j = 0; j < layers.get(l + 1).size(); ++j)
					out.print(w[i][j] + " ");
		}

		out.close();
	}

	public void forwardPropagate()
	{
		for (int l = 0; l < layers.size() - 1; ++l)
			forwardPropagateLayer(layers.get(l), layers.get(l + 1), weights.get(l));
	}

	public void backPropagate(double[] target, double learningSpeed)
	{
		double[] oError = calculateError(output(), target, learningSpeed);

		for (int l = layers.size() - 2; l >= 0; --l)
		{
			double[] iError = backPropagateLayer(layers.get(l), layers.get(l + 1), weights.get(l), oError, learningSpeed);

			updateBias(weights.get(l), oError);

			oError = iError;
		}
	}

	private void forwardPropagateLayer(Layer former, Layer next, double[][] w)
	{
		// Initialise inputs to the next layer to zero
		for (int o = 0; o < next.size(); ++o)
		{
			double tmp = 0;

			// add weighted inputs
			for (int i = 0; i < former.size(); ++i)  
				tmp += w[i][o] * former.output(i);

			// add bias and calculate output
			tmp += w[former.size()][o];

			next.setInput(o, tmp);
		}
	}

	private double[] calculateError(Layer output, double[] target, double learningSpeed)
	{
		double[] oError = new double[output.size()];

		for (int o = 0; o < output.size(); ++o)
			oError[o] = learningSpeed * (target[o] - output.output(o)) * output.derivative(o);

		return oError;
	}

	private double[] backPropagateLayer(Layer former, Layer next, double[][] w, double[] oError, double learningSpeed)
	{
		double[] iError = new double[former.size()];

		for (int i = 0; i < former.size(); ++i)
		{
			double input_error = 0;

			for (int o = 0; o < next.size(); ++o)
			{
				// Get the error
				input_error += w[i][o] * oError[o];

				// Update the weight
				if (former.output(i) != 0.0)
					w[i][o] += former.output(i) * oError[o];
			}

			// Pass the error through the layers function
			iError[i] = input_error * former.derivative(i);
		}

		return iError;
	}

	private void updateBias(double[][] w, double[] oError)
	{
		for (int o = 0; o < w[w.length - 1].length; ++o)
			w[w.length - 1][o] += oError[o];
	}

	private void randomizeWeights(double range)
	{
		Random random = new Random();

		for (int l = 0; l < layers.size() - 1; ++l)
			for (int i = 0; i < layers.get(l).size(); ++i)
				for (int j = 0; j < layers.get(l + 1).size(); ++j)
					weights.get(l)[i][j] = (2 * random.nextDouble() - .5) * range;
	}
}
