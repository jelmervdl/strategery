package graph;

class Node<T>
{
	public Pair velocity;

	public Pair position;

	public double mass;

	private T data;

	public Node()
	{
		mass = 1.0;
	}

	public Node(T object)
	{
		mass = 1.0;

		setData(object);
	}

	public void setData(T data)
	{
		this.data = data;
	}

	public T data()
	{
		return data;
	}

	public Pair repulsion(Node other)
	{
		double k = 1, q1 = 1, q2 = 1;

		double dx = position.x - other.position.x;
		double dy = position.y - other.position.y;

		return new Pair(
			(k * q1 * q2) / (dx * dx),
			(k * q1 * q2) / (dy * dy));
	}
}