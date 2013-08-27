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

public class GraphUtil {
	private static final Logger logger = LoggerFactory.getLogger(GraphUtil.class);

	public static void addEdgeIfNonExistent(Graph graph, Edge edge, Vertex outVertex, Vertex inVertex) {
		// TODO check if this works; maybe the graph implementation ignores the
		// edge ids
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

	public static Collection<Edge> getEdgesOfVertex(Vertex v, Direction d, String... labels) {
		List<Edge> edges = new ArrayList<Edge>();
		for (Edge e : v.getEdges(d, labels)) {
			edges.add(e);
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

	public static int getNumberOfEdges(Graph subGraph) {
		return getIterItemCount(subGraph.getEdges().iterator());
	}

	public static int getNumberOfVertices(Graph subGraph) {
		return getIterItemCount(subGraph.getVertices().iterator());
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

	// TODO put this somewhere else
	public static Set<Vertex> getTestVertices(Graph graph) {
		// http://en.wikipedia.org/wiki/Michael_I._Jordan
		// Michael I. Jordan is a leading researcher in machine learning and
		// artificial intelligence.

		String[] resources = new String[] { "Michael_I._Jordan", "Michael_Jordan", "Machine_learning",
				"Artificial_intelligence", "Basketball" };

		Set<Vertex> vertices = new HashSet<>();
		for (String resource : resources) {
			String uri = GraphConfig.DBPEDIA_RESOURCE_PREFIX + resource;
			vertices.add(GraphUtil.getVertexByUri(graph, uri));
		}

		return Collections.unmodifiableSet(vertices);
	}

	private static String getUriOfVertex(Vertex v) {
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

	public static List<Vertex> getVerticesByUri(Graph graph, List<String> uris) {
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

	public static void removeVerticesWithoutUri(Graph graph) {
		for (Vertex v : graph.getVertices()) {
			String uriProperty = v.getProperty(GraphConfig.URI_PROPERTY);
			if (uriProperty == null || uriProperty.toString().isEmpty()) {
				graph.removeVertex(v);
				logger.info("vertex vid: {} has been deleted", v.getId());
			}
		}
	}

}
