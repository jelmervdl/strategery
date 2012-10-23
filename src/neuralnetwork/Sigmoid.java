package neuralnetwork;

public class Sigmoid extends Function
{
	public double output(double input)
	{
		return 1.0 / (1.0 + Math.exp(-input));
	}

	public double derivative(double input, double output)
	{
		return output * (1.0 - output);
	}
}
