package neuralnetwork;

public class Layer
{
	double[] in;

	double[] out;

	int size;

	Function function;

	String name;

	public Layer(int size, Function function, String name)
	{
		this.size = size;

		this.function = function;

		in = new double[size];

		out = new double[size];

		this.name = name;
	}

	public int size()
	{
		return size;
	}

	public String getName()
	{
		return name;
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
			throw new RuntimeException("Layer " + getName() + " expects " + size + " inputs, but got only " + values.length);

		for (int i = 0; i < size; ++i)
			setValue(i, values[i]);
	}

	public void setValue(int n, double value)
	{
		// if (value < -1.0 || value > 1.0)
		// 	throw new RuntimeException("Cannot set value " + value + " to layer " + getName() + ":" + n);

		in[n] = value;

		out[n] = function.output(value);

		if (out[n] < -1.0 || out[n] > 1.0)
			throw new RuntimeException("Cannot set value " + value + " to layer " + getName() + ":" + n + " because the output falls out of range <-1,1>");
	}

	public double getDerivative(int n)
	{
		return function.derivative(in[n], out[n]);
	}
}
