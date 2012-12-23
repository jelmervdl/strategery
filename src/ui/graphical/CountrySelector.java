package ui.graphical;

import game.Country;

import java.util.HashSet;
import java.util.Set;

class CountrySelector implements MapPanel.ActionListener
{
	Set<Country> options;

	Country selection;

	MapPanel panel;

	boolean isCancelled;

	public CountrySelector(MapPanel panel)
	{
		this.panel = panel;
	}

	public void actionPerformed(MapPanel.ActionEvent event)
	{
		if (event.getCountry() != null && options.contains(event.getCountry()))
			setSelection(event.getCountry());
	}

	public void setOptions(Set<Country> options)
	{
		if (options.size() == 0)
			throw new RuntimeException("Options set is empty");

		this.options = options;
	}

	public boolean hasSelection()
	{
		return selection != null;
	}

	public Country getSelection()
	{
		return selection;
	}

	public void setSelection(Country country)
	{
		if (!options.contains(country))
			throw new RuntimeException("Country not one of the possible options");

		selection = country;
	}

	public void run(Thread gameThread)
	{
		selection = null;
		isCancelled = false;

		panel.addActionListener(this);
		panel.setHighlights(options);

		try {
			while (!hasSelection() && !isCancelled)
				gameThread.sleep(50);
		} catch (InterruptedException e) {
			//
		}

		panel.setHighlights(new HashSet<Country>());
		panel.removeActionListener(this);
	}

	public void cancel()
	{
		isCancelled = true;
	}

	public boolean isCancelled()
	{
		return isCancelled;
	}
}
