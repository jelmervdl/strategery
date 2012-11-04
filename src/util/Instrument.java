package util;

import java.util.ArrayList;

public class Instrument
{
	private ArrayList<Double> values;

	public Instrument()
	{
		reset();
	}

	public void reset()
	{
		values = new ArrayList<Double>();
	}

	public void add(double value)
	{
		values.add(value);
	}

	public double sum()
	{
		double sum = 0;

		for (Double value : values)
			sum += value;

		return sum;
	}

	public double mean()
	{
		return sum() / values.size();
	}

	public double variance()
	{
		double diff = 0;
		double mean = mean();

		for (Double value : values)
			diff += (value - mean) * (value - mean);

		return Math.sqrt(diff / values.size());
	}
}
