package de.unima.dws.dbpediagraph.graphdb.subgraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

/**
 * Represents a path on a graph. The vertices on the path can be explicitly queried to allow lookup in constant time
 * (e.g. for getVertices().contains(v)).
 * 
 * @author Bernhard Schäfer
 * 
 */
class Path {
	private Set<Vertex> vertices;
	private List<Edge> edges;

	public Path() {
		vertices = new HashSet<>();
		edges = new ArrayList<>();
	}

	/**
	 * Copy constructor
	 */
	public Path(Path path) {
		vertices = new HashSet<>(path.vertices);
		edges = new ArrayList<>(path.edges);
	}

	public Path(Vertex start) {
		this();
		vertices.add(start);
	}

	/**
	 * @return the sorted edges on the path
	 */
	public List<Edge> getEdges() {
		return edges;
	}

	public Vertex getLastVertex() {
		if (edges == null || edges.size() == 0)
			if (vertices != null && vertices.size() == 1)
				return vertices.iterator().next();
			else
				return null;
		return edges.get(edges.size() - 1).getVertex(Direction.IN);
	}

	/**
	 * @return the vertices on the path
	 */
	public Set<Vertex> getVertices() {
		return vertices;
	}

	public void setEdges(List<Edge> edges) {
		this.edges = edges;
	}

	public void setVertices(Set<Vertex> vertices) {
		this.vertices = vertices;
	}
}
