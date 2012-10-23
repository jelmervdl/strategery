package neuralnetwork;

public class TanH extends Function
{
	public double output(double input)
	{
		return 2.0 / (1.0 + Math.exp(-input)) - 1.0;
	}

	public double derivative(double input, double output)
	{
		return 1.0 - (output * output);
	}
}
