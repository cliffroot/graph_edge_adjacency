package nifty.packag.plz;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Reader {

	public static void main(String[] args) {
		String[] lines = readFromFile(args[0]);

		Map<Integer, List<Integer>> edgeAdjacency = new LinkedHashMap <Integer, List<Integer>> ();
		Set<Integer> lonelyVertices = new HashSet<Integer>();
		processLines(lines, edgeAdjacency, lonelyVertices);

		List<Integer> names=  new ArrayList<Integer> (edgeAdjacency.entrySet().size());

		for (int i = 0; i < edgeAdjacency.entrySet().size(); i++) {
			names.add(i);
		}

		Graph res = doOneRule(new Graph(), edgeAdjacency, names, 0);

		res.setLonelyVertices(lonelyVertices);

		res.putToFile("/Users/cliffroot/Documents/chewche1.txt");
		System.out.println("Result: " + res);
	}	

	public static Graph doOneRule (Graph graph, Map<Integer, List<Integer>> edgeAdjacency, List<Integer> names, int level) {
		if (isGood(edgeAdjacency, graph.constructEdgeAdjacencyMap())) {
			System.out.println("It's good!");
			return graph;
		}

		Graph copyGraph = graph.copy();
		copyGraph.getPossibleNewEdges(names, edgeAdjacency);

		int x;
		while ((x = copyGraph.addSomeEdge()) != -1) {
			List<Integer> newNames = new ArrayList<> (names);
			newNames.remove(Integer.valueOf(x));
			int d = (Graph.distance(edgeAdjacency, graph.constructEdgeAdjacencyMap()));
			if (d < MIN_DISTANCE) {
				MIN_DISTANCE = d;
			}
			//System.out.println("M:" + MIN_DISTANCE + ", c: " + d) ;
			Graph g = doOneRule(copyGraph, edgeAdjacency, newNames, level + 1);
			if (g != null) {
				return g;
			}
			copyGraph.removeLastEdge();
		}
		return null;
	}

	private static int MIN_DISTANCE = 5000;

	private static boolean isGood (Map<Integer, List<Integer>> originalMap, Map<Integer, Set<Integer>> constructedMap) {
		for (Entry<Integer, List<Integer>> originalAdjacency: originalMap.entrySet()) {
			if (constructedMap.get(originalAdjacency.getKey()) == null) {
				return false;
			}
			if (originalAdjacency.getValue().size() != constructedMap.get(originalAdjacency.getKey()).size()) {
				return false;
			}
		}

		return true;
	}


	public static void processLines (String[] lines, Map<Integer ,List<Integer>> edges, Set<Integer> lonelyVertices) {
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			String [] tokens = line.split(" ");
			if (tokens.length == 1) {
				System.out.println("process lonely vertex!!");
				processLonelyVertex(tokens, lonelyVertices);
			} else {
				processEdgeAdjacency(tokens, edges, i);
			}
		}
	}	

	private static void processEdgeAdjacency(String[] tokens, Map<Integer, List<Integer>> edges, int edgeNumber) {
		assert(tokens != null && tokens.length > 1);

		List<Integer> adjacentEdges = new LinkedList<Integer>();
		int number = 0;
		for (String token: tokens) {
			if (Integer.valueOf(token).equals(Integer.valueOf(1))) {
				adjacentEdges.add(number);
			}
			number++;
		}

		edges.put(edgeNumber, adjacentEdges);
	}

	private static void processLonelyVertex(String[] tokens, Set<Integer> vertices) {
		assert(tokens != null && tokens.length == 1);
		vertices.add(Integer.valueOf(tokens[0]));
		System.out.println("LV: " + vertices);
	}

	private static String[] readFromFile (String name) {
		assert(name != null);
		File source;
		try (FileInputStream fis = new FileInputStream(source = new File(name))) {
			byte[] buffer = new byte[(int) source.length()];
			fis.read(buffer);
			String[] lines = new String(buffer).split("\n");
			return lines;
		} catch (FileNotFoundException fnfex) {
			System.out.println("йой а де файлік :(");
			fnfex.printStackTrace();
		} catch (IOException ioex) {
			System.out.println("та шось нє *yawn*");
			ioex.printStackTrace();
		} 
		return new String[0];
	}


}
