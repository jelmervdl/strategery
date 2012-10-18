package neuralnetwork;

public class Exp extends Function
{
	public double output(double input)
	{
		return (double) Math.exp(input);
	}

	public double derivative(double input, double output)
	{
		return (double) Math.exp((double) input);
	}
}