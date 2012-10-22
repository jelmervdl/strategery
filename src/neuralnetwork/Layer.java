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

	public double[] getValues()
	{
		return out;
	}

	public double getValue(int index)
	{
		return out[index];
	}

	public void setValues(double[] values)
	{
		if (values.length != size)
			throw new RuntimeException("Layer expects " + size + " inputs, but got only " + values.length);

		for (int i = 0; i < size; ++i)
			setValue(i, values[i]);
	}

	public void setValue(int n, double value)
	{
		in[n] = value;
		out[n] = function.output(value);
	}

	public double getDerivative(int n)
	{
		return function.derivative(in[n], out[n]);
	}
}
