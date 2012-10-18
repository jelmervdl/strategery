package neuralnetwork;

public abstract class Function
{
	abstract public double output(double input);

	abstract public double derivative(double input, double output);
}
