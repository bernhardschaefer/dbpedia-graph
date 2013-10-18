package de.unima.dws.dbpediagraph.graphdb.subgraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Path<V, E> {
	private Set<V> vertices;
	private List<E> edges;

	public Path() {
		vertices = new HashSet<>();
		edges = new ArrayList<>();
	}

	/**
	 * Copy constructor
	 */
	public Path(Path<V, E> path) {
		vertices = new HashSet<>(path.vertices);
		edges = new ArrayList<>(path.edges);
	}

	public List<E> getEdges() {
		return edges;
	}

	public Set<V> getVertices() {
		return vertices;
	}

	public void setEdges(List<E> edges) {
		this.edges = edges;
	}

	public void setVertices(Set<V> vertices) {
		this.vertices = vertices;
	}
}
