import java.util.Random;
import java.util.Vector;

class GameState
{
	Vector<Country> countries;

	public void apply(Move move)
	{
		// fight battle (if there is one)
		int attackingEyes = rollDice(move.attacker.dice);
		int defendingEyes = rollDice(move.defender.dice);

		// Attacker wins
		if (attackingEyes > defendingEyes)
		{
			// Take the country!
			move.defender.player = move.attacker.player;

			// Assign remaining dice to country
			move.defender.dice = remainingDice(attackingEyes - defendingEyes);
		}

		// It's a draw
		else if (attackingEyes == defendingEyes)
		{
			move.attacker.dice = 1;

			move.defender.dice = 1;
		}

		// Attacker loses
		else
		{
			move.attacker.dice = 1;

			move.defender.dice = remainingDice(attackingEyes - defendingEyes);
		}
	}

	private int rollDice(int dice)
	{
		Random random = new Random();

		int eyes = 0;

		for (int i = 0; i < dice; ++i)
			eyes += 1 + random.nextInt(6);

		return eyes;
	}

	private int remainingDice(int eyes)
	{
		return (int) Math.ceil(eyes / 6.0);
	}
}