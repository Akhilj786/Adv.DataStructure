
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Stack;

/**
 * @author Akhil Jain 
 * School:University of Florida 
 * Email:aakhil@cise.ufl.edu
 * 
 */
public class mst {

	// Class Variable
	public int totalVertices = 0;
	public int density = 0;
	public static int INF = Integer.MAX_VALUE;
	public File input = null;

	// Adjacency List representation of Input graph
	private ArrayList<ArrayList<Edge>> adjacencyGraph;

	public static void main(String args[]) {
		mst p = new mst();

		// Run Mode
		String runMode = args[0];
		long start, end = 0;

		/*
		 * Run Mode selection -r mode for Random Mode in which we form a random
		 * graph and check if connected then use Simple scheme and f-heap scheme
		 * and compare their run time. 
		 * -s mode uses file input and form a graph using simple array and then print MST. 
		 * -f mode uses file input and form a graph using f-heap and then print MST.
		 */
		switch (runMode) {
		case "-r":
			// System.out.println("We are in random generation and test Mode");
			p.totalVertices = Integer.parseInt(args[1]);
			p.density = Integer.parseInt(args[2]);
			p.rGraph(p.density);
			// p.displayCompleteGraph();

			// Simple Scheme
			start = System.currentTimeMillis();
			p.mstSimple(runMode);
			end = System.currentTimeMillis();
			System.out.println("Simple Scheme time(in millisecond) = " + (end - start));

			// f-heap Scheme
			start = System.currentTimeMillis();
			p.mstFHeap(runMode);
			end = System.currentTimeMillis();
			System.out.print("f-heap Scheme time(in millisecond) = " + (end - start) );

			break;
		case "-s":
			p.input = new File(args[1]);
			p.setUpGraph(p.input);
			// p.displayCompleteGraph();
			if(p.isConnected()){
			start = System.currentTimeMillis();
			p.mstSimple(runMode);
			end = System.currentTimeMillis();
			}
			else{
				System.out.println("Given graph is not connected");
			}
			
			// System.out.println("Execution Time is = " + (end - start));
			break;

		case "-f":
			p.input = new File(args[1]);
			p.setUpGraph(p.input);
			// p.displayCompleteGraph();
			if(p.isConnected()){
			start = System.currentTimeMillis();
			p.mstFHeap(runMode);
			end = System.currentTimeMillis();
			}else{
				System.out.println("Given graph is not connected");
			}
			// System.out.println("Execution Time is = " + (end - start));
			break;

		default:
			System.out.println("Invalid Command Line arguments");
			break;

		}

	}

	/*
	 * Used to generate a Random graph. Function generates random edges and cost
	 * and check if that edge is present in the edgeList.If no then add else
	 * discard The process continues till all desired vertices are linked i.e
	 * graph is connected.
	 */

	private void rGraph(int density) {

		long maxEdges = ((totalVertices) * (totalVertices - 1)) / 2;
		long numberofEdges = (density * (maxEdges)) / 100;

		// System.out.print(numberofEdges + " edges ");
		long cnt = 0;
		do {

			cnt = numberofEdges;

			int i, j, cost;
			Random random_gen = new Random();
			Edge ConstructEgde;

			// MY additions for adjGraph building
			this.setGraphSize(totalVertices);
			int counter = 0;
			// !=0
			while (counter < numberofEdges) {

				i = random_gen.nextInt(totalVertices);
				j = random_gen.nextInt(totalVertices);
				if (i != j) {
					cost = random_gen.nextInt(1000) + 1;
					ConstructEgde = new Edge(i, j, cost);
					// if(

					if (true == this.addEdgetoGraph(ConstructEgde)) {
						ConstructEgde = new Edge(j, i, cost);
						addEdgetoGraph(ConstructEgde);
						counter++;
					}

				}

			}
			// System.out.println("Counter is " + counter);
		} while (!isConnected());

	}

	/*
	 * Simple Scheme MST generation.
	 */

	private void mstSimple(String mode) {

		int distance[] = new int[totalVertices]; // Cost Array
		int visited[] = new int[totalVertices]; // Vertex visited
		int path[] = new int[totalVertices]; // MST Path

		// Setting default values
		for (int i = 0; i < totalVertices; i++) {
			distance[i] = INF;
			visited[i] = 0;
		}

		// Making initial vertex as source and setting its appropriate values.
		distance[0] = 0;
		int current = 0;
		ArrayList<Edge> temp;
		int count = 0;

		while (count != totalVertices) {
			temp = adjacencyGraph.get(current);
			Edge tempedge;
			for (Iterator<Edge> it = temp.iterator(); it.hasNext();) {
				tempedge = it.next();
				// Decrease Key
				if (visited[tempedge.getSecondValue()] != 1)

					if (distance[tempedge.getSecondValue()] > tempedge
							.getEdgeCost()) {
						distance[tempedge.getSecondValue()] = tempedge
								.getEdgeCost();
						path[tempedge.getSecondValue()] = current;
					}
			}

			// Extracting next minimum
			int min = INF;
			for (int i = 0; i < totalVertices; i++) {
				if (visited[i] != 1) {
					if (distance[i] < min) {
						min = distance[i];
						current = i;
					}
				}
			}
			visited[current] = 1;
			count++;

		}
		int minSTCost = 0; // MST Cost

		for (int j = 0; j < totalVertices; j++) {
			minSTCost += distance[j];
			// System.out.println(distance[j]);
		}
		if (mode.equals("-r")) {
		} else {
			System.out.println(minSTCost);
			for (int k = 1; k < totalVertices; k++)
				System.out.println(path[k] + " " + k); // MST Edges
		}

	}

	/*
	 * f-heap Scheme MST generation.
	 */
	private void mstFHeap(String mode) {

		Node<Integer>[] lookup = new Node[totalVertices];
		Fheap<Integer> hp = new Fheap<Integer>();

		lookup[0] = hp.enQueue(0, 0.0);

		for (int i = 1; i < totalVertices; i++) {
			lookup[i] = hp.enQueue(i, INF);
		}

		int distance[] = new int[totalVertices]; // Cost Array
		int visited[] = new int[totalVertices]; // Visited Vertex
		int path[] = new int[totalVertices]; // MST Path

		// Setting default values
		for (int i = 0; i < totalVertices; i++) {
			distance[i] = INF;
			visited[i] = 0;
		}

		// Making initial vertex as source and setting its appropriate values.
		distance[0] = 0;
		int current = 0;

		ArrayList<Edge> temp;
		int count = 0;
		Edge tempedge;
		Node<Integer> ele;

		while (count != totalVertices) {
			temp = adjacencyGraph.get(current);

			for (Iterator<Edge> it = temp.iterator(); it.hasNext();) {
				tempedge = it.next();
				// Decrease Key
				if (visited[tempedge.getSecondValue()] != 1)

					if (lookup[tempedge.getSecondValue()].mPriority > tempedge
							.getEdgeCost()) {
						hp.decreaseKey(lookup[tempedge.getSecondValue()],
								tempedge.getEdgeCost());
						path[tempedge.getSecondValue()] = current;
					}
			}

			// Extracting next Minimum
			ele = hp.dequeueMin();
			distance[(int) ele.mElem] = (int) ele.mPriority;
			current = (int) ele.mElem;

			visited[current] = 1;
			count++;

		}
		int minCost = 0;// MST Cost

		for (int j = 0; j < totalVertices; j++) {
			minCost += distance[j];
		}

		if (mode.equals("-r")) {
		} else {
			System.out.println(minCost);
			for (int k = 1; k < totalVertices; k++)
				System.out.println(path[k] + " " + k);// MST Edges
		}

	}

	/*
	 * Adds edge to a graph
	 * 
	 * @parameter Edge that needs to be checked
	 * 
	 * @return true if edge gets added to adjacencyGraph else false.
	 */
	public boolean addEdgetoGraph(Edge newEdge) {
		boolean alreadyinGraph = false;
		Edge Tempedge;

		if (adjacencyGraph.get(newEdge.getFirstValue()) == null) {

			ArrayList<Edge> temp = new ArrayList<Edge>();
			temp.add(newEdge);
			adjacencyGraph.set(newEdge.getFirstValue(), temp);

			return true;

		} else {
			ArrayList<Edge> temp = adjacencyGraph.get(newEdge.getFirstValue());
			for (Iterator<Edge> iterator = temp.iterator(); iterator.hasNext();) {
				Tempedge = iterator.next();
				if (Tempedge.getSecondValue() == newEdge.getSecondValue())
					alreadyinGraph = true;
			}

			if (alreadyinGraph == false) {

				temp.add(newEdge);
				adjacencyGraph.set(newEdge.getFirstValue(), temp);

				return true;
			} else
				return false;
		}
	}

	/*
	 * Initialize graph
	 * 
	 * @parameter File containing total vertices,edges and edge details
	 */
	public void setUpGraph(File input) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(input));
			String str[] = in.readLine().split(" ");
			totalVertices = Integer.parseInt(str[0]);
			// Initialize size and adds vertices
			this.setGraphSize(totalVertices);

			int numberofEdges = Integer.parseInt(str[1]);
			Edge ConstructEgde;

			while (numberofEdges != 0) {

				str = in.readLine().split(" ");
				ConstructEgde = new Edge(Integer.parseInt(str[0]),
						Integer.parseInt(str[1]), Integer.parseInt(str[2]));
				this.addEdgetoGraph(ConstructEgde);
				ConstructEgde = new Edge(Integer.parseInt(str[1]),
						Integer.parseInt(str[0]), Integer.parseInt(str[2]));
				this.addEdgetoGraph(ConstructEgde);

				numberofEdges--;
			}

			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Graph Connectivity using DFS
	 * 
	 * @return boolean stating whether graph is connected or not
	 */
	private boolean isConnected() {

		int visitedVertex[] = new int[totalVertices];
		Stack<Integer> unVisitedStack = new Stack<Integer>();
		unVisitedStack.push(0);
		int currentVertex;
		// Check until every vertex is visited
		while (!unVisitedStack.isEmpty()) {
			currentVertex = unVisitedStack.pop();

			// If this vertex isn't visited then do this
			if (visitedVertex[currentVertex] == 0) {
				ArrayList<Edge> curVertex = adjacencyGraph.get(currentVertex);
				if (curVertex != null) {
					for (Iterator<Edge> it = curVertex.iterator(); it.hasNext();) {
						Edge edges22 = it.next();
						unVisitedStack.push(edges22.getSecondValue());
					}
				}
			}
			// Make current vertex as visited
			visitedVertex[currentVertex] = 1;
		}

		// Check if all the vertex are visited or not
		for (int i = 0; i < visitedVertex.length; i++) {
			// False Case
			if (visitedVertex[i] != 1)
				return false;
		}

		return true;
	}

	/*
	 * Display Entire graph
	 */
	private void displayCompleteGraph() {

		for (int i = 0; i < totalVertices; i++) {

			ArrayList<Edge> x = this.adjacencyGraph.get(i);
			if (x != null) {
				System.out.print("Vertex(" + i + ")" + "--->");
				for (Iterator<Edge> it = x.iterator(); it.hasNext();) {
					Edge edges22 = it.next();
					System.out.print("  " + edges22.getFirstValue() + ","
							+ edges22.getSecondValue() + " = "
							+ edges22.getEdgeCost() + "  ");
				}
				System.out.println();
			}

		}
	}

	/*
	 * Sets graph size and adds them to adjacencyGraph list
	 */
	public void setGraphSize(int n) {
		adjacencyGraph = new ArrayList<ArrayList<Edge>>(n);
		for (int i = 0; i < n; i++) {
			adjacencyGraph.add(null);
		}
	}

}
