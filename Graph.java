package nifty.packag.plz;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Graph {

	public final static Integer DEFAULT_VERTEX = 0;
	private Integer previousVertex = DEFAULT_VERTEX;

	Set<Vertex> vertices = new HashSet<Vertex>();
	Set<Edge> 	edges    = new HashSet<Edge>  ();


	private static class Vertex {
		Integer value;

		public Vertex(int v) {
			value = v;
		}

		@Override
		public String toString() {
			return "Vertex [value=" + value + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			Vertex other = (Vertex) obj;
			if (value == null) {
				if (other.value != null) {
					return false;
				}
			} else if (!value.equals(other.value)) {
				return false;
			}
			return true;
		}


	}

	public Graph copy () {
		Graph graph = new Graph();
		graph.previousVertex = new Integer(previousVertex);
		graph.edges = new HashSet<Edge>(edges);
		graph.vertices = new HashSet<Vertex>(vertices);

		return graph;
	}

	private static class Edge {
		Vertex start;
		Vertex end;

		Integer name;

		public Edge (Vertex start, Vertex end, Integer name) {
			this.start = start;
			this.end = end;

			this.name = name;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((end == null) ? 0 : end.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((start == null) ? 0 : start.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			Edge other = (Edge) obj;
			if (end == null) {
				if (other.end != null) {
					return false;
				}
			} else if (!end.equals(other.end)) {
				return false;
			}
			if (name == null) {
				if (other.name != null) {
					return false;
				}
			} else if (!name.equals(other.name)) {
				return false;
			}
			if (start == null) {
				if (other.start != null) {
					return false;
				}
			} else if (!start.equals(other.start)) {
				return false;
			}
			return true;
		}

		@Override
		public String toString() {
			return "Edge [start=" + start + ", end=" + end + ", name=" + name + "]";
		}


	}

	public void createOriginalEdge (Integer name) {
		Vertex v1 = getNewVertex();
		Vertex v2 = getNewVertex();

		vertices.add(v1);
		vertices.add(v2);

		edges.add(new Edge(v1, v2, name));
	}

	public Map<Integer, Set<Integer>> constructEdgeAdjacencyMap () {
		Map <Integer, Set<Integer>> result = new HashMap <>();


		for (Edge edge: edges) {
			Set<Integer> adjacencyList = new HashSet<>();
			for (Edge otherEdge: edges) {
				if (edge.equals(otherEdge)) {
					continue;
				}

				if (isAdjacent(edge, otherEdge)) {
					adjacencyList.add(otherEdge.name);
				}
			}
			result.put(edge.name, adjacencyList);
		}

		return result;
	}

	private boolean isAdjacent (Edge edge, Edge otherEdge) {
		return edge.start.equals(otherEdge.start) || edge.end.equals(otherEdge.end) || edge.start.equals(otherEdge.end) || edge.end.equals(otherEdge.start);
	}

	private Vertex getNewVertex () {
		return new Vertex(previousVertex++);
	}

	public List<Integer> getEdges () {
		List<Integer> result = new LinkedList<> ();

		for (Edge edge: edges) {
			result.add(edge.name);
		}

		return result;
	}

	public List<Integer> constructEdgesAccordingToPermutation(List<Integer> perm, List<Integer> adjacent, Integer name) {
		assert(perm.size() == adjacent.size());

		List<Integer> result = new ArrayList<Integer>();
		Edge thisEdge = findEdgeByName(name);
		int i = 0;
		for (Integer p: perm) {
			if (getEdges().contains(adjacent.get(i))) {
				i++;
				continue;
			}
			Vertex newVertex = getNewVertex();
			vertices.add(newVertex);
			Edge newEdge = new Edge(p.equals(Integer.valueOf(0))? thisEdge.start: 
				thisEdge.end, 
				newVertex, adjacent.get(i));

			System.out.println("Added edge: " + newEdge);
			result.add(adjacent.get(i));
			edges.add(newEdge);
			i++;
		}
		return result;
	}

	private Edge findEdgeByName(Integer name) {
		for (Edge edge: edges) {
			if (edge.name.equals(name)) {
				return edge;
			}
		}

		return null;
	}

	@Override
	public String toString() {
		return "Graph [vertices=" + vertices + ", edges=" + edges + "]";
	}

	private static class NewEdgeVariant {
		private Edge newEdge;
		private Vertex[] vertices;

		public NewEdgeVariant(Edge newEdge, Vertex[] vertices) {
			super();
			this.newEdge = newEdge;
			this.vertices = vertices;
		}


	}

	private int variantCounter;
	private List<NewEdgeVariant> variants;

	private NewEdgeVariant last;

	public void getPossibleNewEdges(List<Integer> allNames, final Map<Integer, List<Integer>> canonicRules) {
		variants = new ArrayList<NewEdgeVariant>();

		for (Vertex v: vertices) {
			for (int name: allNames) {
				Vertex v1 = new Vertex(previousVertex + 1);
				Edge newPossible = new Edge(v, v1, name);

				variants.add(new NewEdgeVariant(newPossible, new Vertex[]{v1}));
			}
		}

		for (Vertex v1: vertices) {
			for (Vertex v2: vertices) {
				if (!v1.equals(v2)) {
					for (int name: allNames) {
						Edge newPossible = new Edge(v1, v2, name);

						variants.add(new NewEdgeVariant(newPossible, new Vertex[]{}));
					}
				}
			}
		}

		for (int name: allNames) {
			Vertex v1 = new Vertex(previousVertex + 1);
			Vertex v2 = new Vertex(previousVertex + 2);
			Edge newPossible = new Edge(v1, v2, name);

			variants.add(new NewEdgeVariant(newPossible, new Vertex[]{v1, v2}));
		}


		Collections.sort(variants, new Comparator<NewEdgeVariant>() {

			@Override
			public int compare(NewEdgeVariant nev1, NewEdgeVariant nev2) {
				Graph graph1 = Graph.this.copy();
				Graph graph2 = Graph.this.copy();

				processNev(nev1, graph1);
				processNev(nev2, graph2);

				int dis1 = Graph.distance(canonicRules, graph1.constructEdgeAdjacencyMap());
				int dis2 = Graph.distance(canonicRules, graph2.constructEdgeAdjacencyMap());

				return Integer.compare(dis1, dis2);
			}

		});

		variantCounter = 0;
	}

	public static int distance (Map <Integer, List<Integer>> from, Map <Integer, Set<Integer>> to) {
		int result = 0;
		for (Entry <Integer, List<Integer>> entry: from.entrySet()) {
			Set<Integer> list = to.get(entry.getKey());
			if (list == null) {
				result += entry.getValue().size();
			} else {
				for (Integer i: entry.getValue()) {
					if (!list.contains(i)) {
						result += 1;
					}
				}
			}
		}
		//System.out.println("Distance from: " + from + ", to: " + to + " == " + result);
		return result;
	}

	public void processNev(NewEdgeVariant nev, Graph graph) {
		graph.edges.add(nev.newEdge);
		graph.vertices.addAll(Arrays.asList(nev.vertices));
		graph.previousVertex += nev.vertices.length;
	}

	public int addSomeEdge() {
		if (variantCounter >= variants.size()) {
			return -1;
		} else {

			NewEdgeVariant nev = variants.get(variantCounter);
			processNev(nev, this);

			variantCounter++;

			last = nev;
			return nev.newEdge.name;
		}
	}

	public void removeLastEdge () {
		edges.remove(last.newEdge);
		for (Vertex v: last.vertices) {
			vertices.remove(v);
		}

		previousVertex -= last.vertices.length;
	}

	public void putToFile(String string) {
		try (FileOutputStream fos = new FileOutputStream(new File(string))) {
			for (Vertex vertex: vertices) {
				fos.write((vertex.value + ": ").getBytes());
				StringBuilder sb = new StringBuilder();
				for (Edge edge: edges) {
					if (edge.start.equals(vertex)) {
						sb.append(edge.end.value + ",");
					}
					if (edge.end.equals(vertex)) {
						sb.append(edge.start.value + ",");
					}
				}
				sb.reverse();
				sb.replace(0, 1, "\n");
				sb.reverse();

				fos.write(sb.toString().getBytes());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
