package graph;

class Edge
{
	public Node a;

	public Node b;

	private double k;

	public Edge(Node a, Node b)
	{
		this.a = a;
		this.b = b;
		this.k = 1;
	}

	public boolean connectsTo(Node node)
	{
		return node == a || node == b;
	}

	public Pair attraction()
	{
		return a.position.subtract(b.position).multiply(k);
	}
}
