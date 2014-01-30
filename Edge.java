/*
 * Defines edges between 2 vertices.
 * Edge have a cost on them.
 */
public class Edge {
	private int firstvalue, SecondValue, edgeCost;

	public Edge() {

	}
	//Parameterized Constructor
	public Edge(int firstvalue, int SecondValue, int weight) {
		this.firstvalue = firstvalue;
		this.SecondValue = SecondValue;
		this.edgeCost = weight;
	}
	// Getter and Setter
	public int getFirstValue() {
		return firstvalue;
	}

	public void setFirstValue(int firstvalue) {
		this.firstvalue = firstvalue;
	}

	public int getSecondValue() {
		return SecondValue;
	}

	public void setSecondValue(int SecondValue) {
		this.SecondValue = SecondValue;
	}

	public int getEdgeCost() {
		return edgeCost;
	}

	public void setEdgeCost(int edgeCost) {
		this.edgeCost = edgeCost;
	}

	@Override
	public boolean equals(Object obj) {
		Edge e = (Edge) obj;
		if (this.getEdgeCost() == e.getEdgeCost()
				&& this.getFirstValue() == e.getFirstValue()
				&& this.getSecondValue() == e.getSecondValue())
			return true;
		else
			return false;
	}
}