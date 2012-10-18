package neuralnetwork;

public class Linear extends Function
{
	public double output(double input)
	{
		return input;
	}

	public double derivative(double input, double output)
	{
		return 1.0;
	}
}
