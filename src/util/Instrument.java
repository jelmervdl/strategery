package util;

import java.util.ArrayList;

public class Instrument
{
	private double[] values;

	private int index;

	public Instrument(int capacity)
	{
		reset(capacity);
	}

	public void reset(int capacity)
	{
		values = new double[capacity];

		index = 0;
	}

	public void add(double value)
	{
		values[index++ % values.length] = value;
	}

	public double[] toArray()
	{
		double[] array = new double[count()];

		for (int i = 0; i < count(); ++i)
			array[i] = values[i];

		return array;
	}

	public int getIndex()
	{
		return index - 1;
	}

	public int getCapacity()
	{
		return values.length;
	}

	public int count()
	{
		return Math.min(index, values.length);
	}

	public double sum()
	{
		double sum = 0;

		for (int i = 0; i < count(); ++i)
			sum += values[i];

		return sum;
	}

	public double mean()
	{
		return sum() / count();
	}

	public double variance()
	{
		double diff = 0;
		double mean = mean();

		for (int i = 0; i < count(); ++i)
			diff += (values[i] - mean) * (values[i] - mean);

		return Math.sqrt(diff / count());
	}

	public double getLast()
	{
		return values[index - 1 % values.length];
	}
}
