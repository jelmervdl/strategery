package neuralnetwork;

public class Layer
{
	double[] in;

	double[] out;

	int size;

	Function function;

	public Layer(int size, Function function)
	{
		this.size = size;

		this.function = function;

		in = new double[size];

		out = new double[size];
	}

	public int size()
	{
		return size;
	}

	public double[] values()
	{
		return out;
	}

	public void setValues(double[] values)
	{
		if (values.length != size)
			throw new RuntimeException("Layer expects " + size + " inputs, but got only " + values.length);

		in = values;

		for (int i = 0; i < size; ++i)
			out[i] = function.output(in[i]);
	}

	public double output(int n)
	{
		return out[n];
	}

	public double derivative(int n)
	{
		return function.derivative(in[n], out[n]);
	}

	public void setInput(int n, double value)
	{
		in[n] = value;
		out[n] = function.output(value);
	}
}
