package util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.HashMap;
import java.util.Locale;
import java.util.Scanner;
import java.util.NoSuchElementException;

public class Configuration
{
	private HashMap<String, Object> data;

	private String prefix;

	public Configuration()
	{
		data = new HashMap<String,Object>();
		prefix = "";
	}

	public Configuration(Configuration other)
	{
		data = new HashMap<String,Object>(other.data);
		prefix = other.prefix;
	}

	public Configuration getSection(String name)
	{
		Configuration section = new Configuration();
		section.data = data;
		section.prefix = prefix + name + ".";

		return section;
	}

	public int getInt(String key)
	{
		return (Integer) data.get(prefix + key);
	}

	public boolean getBoolean(String key)
	{
		return (Boolean) data.get(prefix + key);
	}

	public double getDouble(String key)
	{
		return (Double) data.get(prefix + key);
	}

	public String getString(String key)
	{
		return (String) data.get(prefix + key);
	}

	public int getInt(String key, int fallback)
	{
		return data.containsKey(prefix + key) ? getInt(key) : fallback; 
	}

	public boolean getBoolean(String key, boolean fallback)
	{
		return data.containsKey(prefix + key) ? getBoolean(key) : fallback; 
	}

	public double getDouble(String key, double fallback)
	{
		return data.containsKey(prefix + key) ? getDouble(key) : fallback;
	}

	public String getString(String key, String fallback)
	{
		return data.containsKey(prefix + key) ? getString(key) : fallback;
	}
	
	public void read(File file) throws IOException
	{
		FileInputStream fstream = new FileInputStream(file);
	 
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		String prefix = new String();
		String line;
		int index = 0;
		while ((line = br.readLine()) != null)  
		{
			index++;
			line = line.trim();
			
			// Skip comments and white lines.
			if (line.startsWith("#") || line.equals(""))
				continue;

			// Read section headers
			if (line.startsWith("[") && line.endsWith("]"))
			{
				prefix = line.substring(1, line.length() - 1);
				continue;
			}

			try {
				Scanner scanner = new Scanner(line);
                scanner.useLocale(Locale.US);

				if (!scanner.hasNext())
					throw new NoSuchElementException("key string");

				String name = scanner.next();

				if (!prefix.equals(""))
					name = prefix + "." + name;

				if (!scanner.hasNext())
					throw new NoSuchElementException("type string");

				String type = scanner.next();
				
				if (type.equals("b"))
				{
					if (!scanner.hasNextBoolean())
						throw new NoSuchElementException("value boolean");

					data.put(name, new Boolean(scanner.nextBoolean()));
				}
				else if (type.equals("i"))
				{
					if (!scanner.hasNextInt())
						throw new NoSuchElementException("value integer");

					data.put(name, new Integer(scanner.nextInt()));
				}
				else if (type.equals("d"))
				{
					if (!scanner.hasNextDouble())
						throw new NoSuchElementException("value double");

					data.put(name, new Double(scanner.nextDouble()));
				}
				else if (type.equals("s"))
				{
					if (!scanner.hasNext())
						throw new NoSuchElementException("value string");

					data.put(name, scanner.next());
				}
				else
				{
						System.err.println("Unknown type '" + type + "'");
				}
			}
			catch (NoSuchElementException e)
			{
				throw new RuntimeException("Could not parse line " + index + " of " + file + ": " + e.getMessage());
			}
		}
	}
}
