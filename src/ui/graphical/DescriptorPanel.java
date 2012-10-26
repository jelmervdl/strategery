package ui.graphical;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Label;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import descriptors.Descriptor;

import game.GameState;
import game.Player;

class DescriptorPanel extends JPanel
{
	Map<Descriptor, TableRow> descriptors;

	private class TableRow extends Container
	{
		public Label nameLabel;

		public Label valueLabel;

		public TableRow(Descriptor descriptor)
		{
			super();

			nameLabel = new Label();
			nameLabel.setText(descriptor.getClass().getName() + ":");

			valueLabel = new Label();
			
			setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
			add(nameLabel);
			add(valueLabel);
		}

		public void update(double value)
		{
			valueLabel.setText(Double.toString(value));
		}
	}

	public DescriptorPanel(List<Descriptor> descriptors)
	{
		super();
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		this.descriptors = new HashMap<Descriptor, TableRow>();

		for (Descriptor descriptor : descriptors)
		{
			TableRow row = new TableRow(descriptor);
			this.descriptors.put(descriptor, row);
			add(row);
		}
	}

	public void update(GameState state, Player player)
	{
		for (Descriptor descriptor : descriptors.keySet())
			descriptors.get(descriptor).update(descriptor.describe(state, player));
	}
}
