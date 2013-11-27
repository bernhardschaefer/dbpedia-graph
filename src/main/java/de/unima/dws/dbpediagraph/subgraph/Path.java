package de.unima.dws.dbpediagraph.subgraph;

import java.util.*;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graph.Graphs;

/**
 * Represents a path on a graph. The vertices on the path can be explicitly queried to allow lookup in constant time
 * (e.g. for getVertices().contains(v)).
 * 
 * @author Bernhard Schäfer
 * 
 */
class Path {
	/**
	 * Construct a new path object from a given path and a new hop to a adjacent node.
	 * 
	 * @param path
	 *            the given path
	 * @param edge
	 *            the edge that has been taken as next hop
	 * @param child
	 *            the new node that has been reached using the provided edge
	 * @return new path object
	 */
	public static Path newHop(Path path, Edge edge, Vertex child) {
		Path newPath = new Path(path);
		newPath.last = child;
		newPath.vertices.add(child);
		newPath.edges.add(edge);
		return newPath;
	}

	private final Set<Vertex> vertices;
	private final List<Edge> edges;
	private final Vertex start;

	private Vertex last;

	/**
	 * Copy constructor
	 */
	public Path(Path path) {
		this.start = path.getStart();
		this.last = path.last;
		vertices = new HashSet<>(path.vertices);
		edges = new ArrayList<>(path.edges);
	}

	/**
	 * Constructs a new path object using the provided start node.
	 */
	public Path(Vertex start) {
		this.start = start;
		this.last = start;
		vertices = new HashSet<>();
		vertices.add(start);
		edges = new ArrayList<>();
	}

	/**
	 * @return the sorted edges on the path
	 */
	public List<Edge> getEdges() {
		return edges;
	}

	public Vertex getLast() {
		return last;
	}

	public Vertex getStart() {
		return start;
	}

	/**
	 * @return the vertices on the path
	 */
	public Set<Vertex> getVertices() {
		return vertices;
	}

	public void setLast(Vertex last) {
		this.last = last;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append("Start: ").append(Graphs.shortUriOfVertex(start))
				.append(" End: ").append(Graphs.shortUriOfVertex(last))
				.append(" Edges: ").append(edges)
				.toString();
	}

}
