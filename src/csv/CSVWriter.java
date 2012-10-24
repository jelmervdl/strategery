package csv;

import java.io.PrintStream;
import java.util.List;

public class CSVWriter
{
	PrintStream out;

	int lineSize;

	public CSVWriter(PrintStream stream)
	{
		out = stream;

		lineSize = -1;
	}

	public void writeln(List values)
	{
		if (lineSize == -1)
			lineSize = values.size();
		else
			if (lineSize != values.size())
				throw new RuntimeException("First line consisted of " + lineSize + " columns, but now trying to print " + values.size() + " columns.");

		for (int i = 0; i < values.size(); ++i)
		{
			if (i > 0)
				out.print(",");

			out.print(escape(values.get(i).toString()));
		}

		out.println();
	}

	private String escape(String value)
	{
		return "\"" + value.replace("\"", "\"\"") + "\"";
	}
}
