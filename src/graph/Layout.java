package graph;

import java.util.*;

class Layout
{
	List<Node> nodes;

	List<Edge> edges;

	public Layout()
	{
		nodes = new Vector<Node>();
		edges = new Vector<Edge>();
	}

	public Layout(List<Node> nodes, List<Edge> edges)
	{
		this.nodes = nodes;
		this.edges = edges;
	}

	public void addNode(Node node)
	{
		nodes.add(node);
	}

	public void addEdge(Edge edge)
	{
		edges.add(edge);
	}

	public void layoutGraph()
	{
		for (Node node : nodes)
		{
			// Set initial node velocity to 0
			node.velocity = new Pair(0, 0);

			// Put each node at a random position
			node.position = Pair.random();
		}

		double totalKineticEnergy;

		double damping = 0.6;

		int timeStep = 0;

		do {
			// Running sum of total kinetic energy over all particles
			totalKineticEnergy = 0;

			for (Node node : nodes)
			{
				// running sum of total force on this particular node
				Pair netForce = new Pair(0, 0);

				for (Node otherNode : nodes)
				{
					if (node == otherNode)
						continue;

					netForce = netForce.add(node.repulsion(otherNode));
				}

				for (Edge edge : connectedEdges(node))
					netForce = netForce.subtract(edge.attraction());
				
				node.velocity = node.velocity.add(netForce.multiply(timeStep)).multiply(damping);
				node.position = node.position.add(node.velocity.multiply(timeStep));
				totalKineticEnergy += node.mass * node.velocity.squared();
			}

			++timeStep;

		} while (totalKineticEnergy < 0.05);
	}

	private List<Edge> connectedEdges(Node node)
	{
		List<Edge> connected = new Vector<Edge>();

		for (Edge edge : edges)
			if (edge.connectsTo(node))
				connected.add(edge);

		return connected;
	}
}