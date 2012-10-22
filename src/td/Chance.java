package td;

public class Chance
{
	final static double[][] chanceTable = {
		{0.4167,    0.0926,    0.0116,    0.0008,    0.0000,         0,         0},
		{0.8380,    0.4437,    0.1520,    0.0359,    0.0061,    0.0008,    0.0001},
		{0.9730,    0.7785,    0.4536,    0.1917,    0.0607,    0.0149,    0.0029},
		{0.9973,    0.9392,    0.7428,    0.4595,    0.2204,    0.0834,    0.0254},
		{0.9998,    0.9879,    0.9093,    0.7181,    0.4637,    0.2424,    0.1036},
		{1.0000,    0.9982,    0.9753,    0.8840,    0.6996,    0.4667,    0.2600},
		{1.0000,    0.9998,    0.9947,    0.9615,    0.8624,    0.6852,    0.4691}
	};

	final static int[][] diceRemainingAttacker = {
		{1,     1,     1,     1,     1,     0,     0},
		{1,     1,     1,     1,     1,     1,     1},
		{1,     1,     1,     1,     1,     1,     1},
		{2,     1,     1,     1,     1,     1,     1},
		{2,     2,     1,     1,     1,     1,     1},
		{3,     2,     2,     2,     1,     1,     1},
		{4,     3,     2,     2,     2,     1,     1}
	};

	final static int[][] diceRemaining = {
		{1,     1,     1,     2,     2,     3,     4},
		{1,     1,     1,     1,     2,     2,     3},
		{1,     1,     1,     1,     1,     2,     2},
		{1,     1,     1,     1,     1,     2,     2},
		{1,     1,     1,     1,     1,     1,     2},
		{0,     1,     1,     1,     1,     1,     1},
		{0,     1,     1,     1,     1,     1,     1}
	};

	// Chance of winning with X dice against Y dice
	public static double chanceTable(int dice1, int dice2)
	{	
		return chanceTable[dice1][dice2];
	}

	public static int diceRemainingAttacker(int dice1, int dice2)
	{
		return diceRemainingAttacker[dice1][dice2];
	}

	public static int diceRemaining(int dice1, int dice2)
	{
		return diceRemaining[dice1][dice2];
	}        
}
