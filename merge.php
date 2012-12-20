#!/usr/bin/php
<?php

class Reader
{
	private $rows = array();

	private $count = 0;

	private $header;

	public function readFile($fhandle)
	{
		$this->header = fgetcsv($fhandle); // skip header

		$i = 0;

		while ($row = fgetcsv($fhandle))
		{
			$this->merge($this->rows[$i], $row);
			++$i;
		}

		// if ($i != count($this->rows))
			// throw new Exception("Not enough rows in file");

		$this->count++;
	}

	public function merge(&$data, $row)
	{
		foreach ($row as $col => $value)
		{
			if (isset($data[$col]))
				$data[$col] += $value;
			else
				$data[$col] = $value;
		}

		if (isset($data[count($row)]))
			++$data[count($row)];
		else
			$data[count($row)] = 1;
	}

	public function renumber()
	{
		$n = 0;

		foreach ($this->rows as &$row)
			$row[0] = ++$n;
	}

	public function write($lines = 0)
	{
		echo implode("\t", $this->header) . "\n";

		$count = $this->count;

		foreach ($this->rows as $row)
		{
			$print = array();
			$last_column = count($row) - 1;

			foreach ($row as $col => $val)
			{
				if ($col == 0 || $col == $last_column)
					$print[] = sprintf('%d', $val);
				else
					$print[] = sprintf('%s', $val / $row[$last_column]);
			}

			printf("%s\n", implode("\t", $print));
			if (--$lines == 0)
				break;
		}
	}
}

$reader = new Reader();

for ($i = 1; $i < $argc; ++$i)
	try {
		$reader->readFile(fopen($argv[$i], 'r'));
	} catch(Exception $e) {
		die("Error while processing {$argv[$i]}: $e\n");
	}

$reader->renumber();

$reader->write();
