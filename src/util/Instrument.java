package util;

import java.util.ArrayList;

public class Instrument
{
	private double[] values;

	private int index;

	private int size;

	public Instrument(int capacity)
	{
		values = new double[capacity];

		reset();
	}

	public void reset()
	{
		index = 0;

		size = 0;
	}

	public void add(double value)
	{
		values[index++ % values.length] = value;

		if (size < values.length)
			size++;
	}

	public int count()
	{
		return size;
	}

	public double sum()
	{
		double sum = 0;

		for (int i = 0; i < size; ++i)
			sum += values[i];

		return sum;
	}

	public double mean()
	{
		return sum() / size;
	}

	public double variance()
	{
		double diff = 0;
		double mean = mean();

		for (int i = 0; i < size; ++i)
			diff += (values[i] - mean) * (values[i] - mean);

		return Math.sqrt(diff / size);
	}
}
