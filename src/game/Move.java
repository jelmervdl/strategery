package game;

public class Move
{
	public Country attackingCountry;

	public Country defendingCountry;

	public Move()
	{
		// do nothing.
	}

	public Move(Country attacker, Country defender)
	{
		this.attackingCountry = attacker;
		this.defendingCountry = defender;
	}

	public Country getAttackingCountry()
	{
		return attackingCountry;
	}

	public Country getDefendingCountry()
	{
		return defendingCountry;
	}

	public boolean isEndOfTurn()
	{
		return this.attackingCountry == null || this.defendingCountry == null;
	}

	public String toString()
	{
		return isEndOfTurn()
			? "[Move end of turn]"
			: "[Move " + attackingCountry + " attacks " + defendingCountry + "]";
	}
}