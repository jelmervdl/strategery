package graph;

import java.util.*;

public class Test
{
	static public void main(String[] args)
	{
		List<Node> nodes = new Vector<Node>();
		List<Edge> edges = new Vector<Edge>();

		for (int i = 0; i <= 5; ++i)
		{
			Node node = new Node();
			nodes.add(node);
		}

		for (int i = 0; i <= 5; ++i)
		{
			Edge edge = new Edge(nodes.get(i), nodes.get((i + 1) % 5));
			edges.add(edge);
		}

		Layout layout = new Layout(nodes, edges);
		layout.layoutGraph();

		for (Node node : nodes)
			System.out.println(node.position);
	}
}