class Move
{
	public Country attackingCountry;

	public Country defendingCountry;

	public Move(Country attacker, Country defender)
	{
		this.attackingCountry = attacker;
		this.defendingCountry = defender;
	}
}