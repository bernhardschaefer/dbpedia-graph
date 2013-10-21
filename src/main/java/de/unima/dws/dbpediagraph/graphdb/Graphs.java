package de.unima.dws.dbpediagraph.graphdb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

/**
 * Noninstantiable utility class for performing various graph operations. All operations are static.
 * 
 * @author Bernhard Schäfer
 * 
 */
public final class Graphs {
	private static final Logger logger = LoggerFactory.getLogger(Graphs.class);

	public static void addEdgeIfNonExistent(Graph graph, Edge edge, Vertex outVertex, Vertex inVertex) {
		if (graph.getEdge(edge.getId()) == null) {
			graph.addEdge(edge.getId(), outVertex, inVertex, edge.getLabel());
		}
	}

	public static void addNodeAndEdgesIfNonExistent(Graph graph, Collection<Edge> edges) {
		for (Edge edge : edges) {
			String outVertexUri = edge.getVertex(Direction.OUT).getProperty(GraphConfig.URI_PROPERTY).toString();
			Vertex outVertex = addVertexByUri(graph, outVertexUri);

			String inVertexUri = edge.getVertex(Direction.IN).getProperty(GraphConfig.URI_PROPERTY).toString();
			Vertex inVertex = addVertexByUri(graph, inVertexUri);

			addEdgeIfNonExistent(graph, edge, outVertex, inVertex);
		}
	}

	/**
	 * Adds a vertex with the uri as property to the graph if it does not exist yet.
	 */
	public static Vertex addVertexByUri(Graph graph, String uri) {
		Vertex v = getVertexByUri(graph, uri);
		if (v == null) {
			v = graph.addVertex(uri);
			v.setProperty(GraphConfig.URI_PROPERTY, uri);
		}
		return v;
	}

	public static void addVerticesByUrisOfVertices(Graph graph, Collection<Vertex> vertices) {
		for (Vertex v : vertices) {
			addVertexByUri(graph, v.getProperty(GraphConfig.URI_PROPERTY).toString());
		}
	}

	public static Iterable<Edge> getConnectedEdges(Vertex current, GraphType graphDirection) {
		switch (graphDirection) {
		case DIRECTED_GRAPH:
			return current.getEdges(Direction.OUT);
		case UNDIRECTED_GRAPH:
			return current.getEdges(Direction.BOTH);
		default:
			throw new IllegalArgumentException("Suitable graph direction is missing: " + graphDirection);
		}
	}

	public static Set<Vertex> getConnectedVerticesBothDirections(Vertex vertex) {
		final Set<Vertex> vertices = new HashSet<Vertex>();
		for (final Edge edge : vertex.getEdges(Direction.BOTH)) {
			Vertex other = Graphs.getOppositeVertexUnsafe(edge, vertex);
			vertices.add(other);
		}
		return vertices;
	}

	public static Collection<Edge> getEdgesOfVertex(Vertex vertex, Direction direction, String... labels) {
		Collection<Edge> edges;
		final Iterable<Edge> itty = vertex.getEdges(direction);
		if (itty instanceof Collection) {
			edges = (Collection<Edge>) itty;
		} else {
			edges = new ArrayList<Edge>();
			for (final Edge edge : itty) {
				edges.add(edge);
			}
		}

		// if direction==BOTH, collapse edges (u,v) and (v,u) to a single edge
		if (direction == Direction.BOTH) {
			// TODO implement
		}

		return edges;
	}

	private static int getIterItemCount(Iterator<?> iter) {
		int counter = 0;
		while (iter.hasNext()) {
			iter.next();
			counter++;
		}
		return counter;
	}

	public static int getNumberOfEdges(Graph subgraph) {
		return getIterItemCount(subgraph.getEdges().iterator());
	}

	public static int getNumberOfVertices(Graph subgraph) {
		return getIterItemCount(subgraph.getVertices().iterator());
	}

	public static Vertex getOppositeVertexSafe(Edge edge, Vertex vertex) {
		Vertex in = edge.getVertex(Direction.IN);
		Vertex out = edge.getVertex(Direction.OUT);
		if (vertex.equals(out)) {
			return in;
		} else if (vertex.equals(in)) {
			return out;
		} else {
			throw new IllegalArgumentException(String.format("Vertex %s is not part of edge %s", vertex.getId(), edge));
		}
	}

	/**
	 * Get the other vertex of the edge. NOTE: To improve performance this method does not check if the provided vertex
	 * actually belongs to the provided edge.
	 */
	public static Vertex getOppositeVertexUnsafe(Edge edge, Vertex vertex) {
		Vertex in = edge.getVertex(Direction.IN);
		if (!vertex.equals(in))
			return in;
		else
			return edge.getVertex(Direction.OUT);
	}

	/**
	 * Reconstruct a path based on a start and end vertex and a map that displays the traversal taken.
	 * 
	 * @param previousMap
	 *            the map that stores the performed traversals. Each entry shows the edge from which the vertex has been
	 *            reached.
	 * @return the found path from start to end vertex as a list of edges.
	 */
	public static List<Edge> getPathFromTraversalMap(Vertex start, Vertex end, Map<Vertex, Edge> previousMap) {
		List<Edge> pathFromEndToStart = new LinkedList<Edge>();
		Vertex previousVertex = end;
		while (!start.equals(previousVertex)) {
			Edge currentEdge = previousMap.get(previousVertex);
			if (pathFromEndToStart.contains(currentEdge)) {
				// we have a cycle
				// return path as currently is
				// TODO investigate proper action
				break;
			}
			pathFromEndToStart.add(currentEdge);
			previousVertex = currentEdge.getVertex(Direction.OUT);
		}
		Collections.reverse(pathFromEndToStart); // path is now in start->end order
		return pathFromEndToStart;
	}

	public static Iterable<Edge> getUntraversedConnectedEdges(Vertex current, List<Edge> edges, GraphType graphDirection) {
		switch (graphDirection) {
		case DIRECTED_GRAPH:
			return current.getEdges(Direction.OUT);
		case UNDIRECTED_GRAPH:
			Collection<Edge> untraversedOutgoingEdges = new ArrayList<Edge>();
			for (Edge edge : current.getEdges(Direction.OUT, GraphConfig.EDGE_LABEL)) {
				if (!edges.contains(edge))
					untraversedOutgoingEdges.add(edge);
			}

			Collection<Edge> untraversedIngoingEdges = new ArrayList<Edge>();
			for (Edge edge : current.getEdges(Direction.IN, GraphConfig.EDGE_LABEL)) {
				if (!edges.contains(edge))
					untraversedIngoingEdges.add(edge);
			}

			untraversedIngoingEdges.addAll(untraversedOutgoingEdges);
			return untraversedIngoingEdges;
		default:
			throw new IllegalArgumentException("Suitable graph direction is missing: " + graphDirection);
		}
	}

	public static String getUriOfVertex(Vertex v) {
		return v.getProperty(GraphConfig.URI_PROPERTY).toString();
	}

	public static Collection<String> getUrisOfVertices(Collection<Vertex> vertices) {
		List<String> uris = new LinkedList<>();
		for (Vertex v : vertices) {
			uris.add(getUriOfVertex(v));
		}
		return uris;
	}

	public static Vertex getVertexByUri(Graph graph, String uri) {
		String shortUri = UriShortener.shorten(uri);
		List<Vertex> vertices = new LinkedList<Vertex>();
		Iterable<Vertex> verticesIter = graph.getVertices(GraphConfig.URI_PROPERTY, shortUri);
		for (Vertex v : verticesIter) {
			vertices.add(v);
		}

		if (vertices.size() == 0) {
			return null;
		}
		if (vertices.size() > 1) {
			logger.warn("There is more than one vertex with the uri " + uri);
			for (Vertex v : vertices) {
				int inDegree = getEdgesOfVertex(v, Direction.IN).size();
				int outDegree = getEdgesOfVertex(v, Direction.OUT).size();
				logger.warn("vid: {} uri: {} inDegree: {} outDegree {} ", v.getId(),
						v.getProperty(GraphConfig.URI_PROPERTY), inDegree, outDegree);
			}
			logger.warn("");
		}

		return vertices.get(0);
	}

	public static String getVertexToString(Vertex v) {
		String uri = v.getProperty(GraphConfig.URI_PROPERTY);
		return String.format("vid: %s uri: %s", v.getId().toString(), uri);
	}

	/**
	 * Converts the uris to vertices. Omits uris that cannot be found in the provided graph.
	 */
	public static List<Vertex> getVerticesByUri(Graph graph, Collection<String> uris) {
		List<Vertex> vertices = new LinkedList<Vertex>();
		for (String uri : uris) {
			Vertex v = getVertexByUri(graph, uri);
			if (v != null) {
				vertices.add(v);
			}
		}
		return vertices;
	}

	public static Set<Vertex> getVerticesOfEdges(Collection<Edge> edges) {
		Set<Vertex> vertices = new HashSet<Vertex>();
		for (Edge e : edges) {
			vertices.add(e.getVertex(Direction.IN));
			vertices.add(e.getVertex(Direction.OUT));
		}
		return vertices;
	}

	public static Collection<Collection<Vertex>> getWordsVerticesByUri(Graph graph,
			Collection<Collection<String>> wordsSensesString) {
		Collection<Collection<Vertex>> wordVertices = new ArrayList<>();
		for (Collection<String> uris : wordsSensesString) {
			wordVertices.add(getVerticesByUri(graph, uris));
		}
		return wordVertices;
	}

	public static boolean isNodeOnPath(Vertex child, Collection<Edge> path) {
		for (Edge edge : path) {
			if (child.equals(edge.getVertex(Direction.IN)) || child.equals(edge.getVertex(Direction.OUT)))
				return true;
		}
		return false;
	}

	public static boolean isVertexInGraph(Vertex v, Graph subGraph) {
		String uri = Graphs.getUriOfVertex(v);
		Vertex subGraphVertex = getVertexByUri(subGraph, uri);
		return subGraphVertex != null;
	}

	public static void removeVerticesWithoutUri(Graph graph) {
		for (Vertex v : graph.getVertices()) {
			String uriProperty = v.getProperty(GraphConfig.URI_PROPERTY);
			if (uriProperty == null || uriProperty.toString().isEmpty()) {
				graph.removeVertex(v);
				logger.info("vertex vid: {} has been deleted", v.getId());
			}
		}
	}

	// Suppress default constructor for noninstantiability
	private Graphs() {
		throw new AssertionError();
	}

}
