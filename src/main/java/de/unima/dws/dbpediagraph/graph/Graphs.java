package de.unima.dws.dbpediagraph.graph;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.*;
import com.tinkerpop.blueprints.oupls.jung.GraphJung;

import de.unima.dws.dbpediagraph.util.CollectionUtils;
import de.unima.dws.dbpediagraph.util.GraphJungUndirectedWrapper;
import de.unima.dws.dbpediagraph.weights.EdgeWeights;

/**
 * Noninstantiable utility class for performing various graph operations. All operations are static.
 * 
 * @author Bernhard Schäfer
 * 
 */
public final class Graphs {
	private static final Logger logger = LoggerFactory.getLogger(Graphs.class);

	/**
	 * The provided vertices need to be part of the provided graph.
	 */
	public static void addEdgeIfNonExistent(Graph graph, Edge edge, Vertex outVertex, Vertex inVertex) {
		if (graph.getVertex(outVertex.getId()) == null || graph.getVertex(inVertex.getId()) == null)
			throw new IllegalArgumentException("Both provided vertices need to be part of the provided graph.");
		addEdgeIfNonExistentUnsafe(graph, edge, outVertex, inVertex);
	}

	private static void addEdgeIfNonExistentUnsafe(Graph graph, Edge edge, Vertex outVertex, Vertex inVertex) {
		if (graph.getEdge(edge.getId()) == null) {
			Edge newEdge = graph.addEdge(edge.getId(), outVertex, inVertex, edge.getLabel());
			newEdge.setProperty(GraphConfig.URI_PROPERTY, edge.getProperty(GraphConfig.URI_PROPERTY));
		}
	}

	public static void addNodeAndEdgesByIdIfNonExistent(Graph graph, Collection<Edge> edges) {
		for (Edge edge : edges) {
			Vertex outVertex = addVertexByIdIfNonExistent(graph, edge.getVertex(Direction.OUT));
			Vertex inVertex = addVertexByIdIfNonExistent(graph, edge.getVertex(Direction.IN));
			addEdgeIfNonExistentUnsafe(graph, edge, outVertex, inVertex);
		}
	}

	public static void addVerticesByIdIfNonExistent(Graph graph, Iterable<Vertex> vertices) {
		for (Vertex v : vertices)
			addVertexByIdIfNonExistent(graph, v);
	}

	public static Vertex addVertexByIdIfNonExistent(Graph graph, Vertex v) {
		// IMPORTANT: Vertex v can be from another graph, and thus is is important not to return v but instead the
		// retrieved vertex from the provided graph.
		Vertex toReturn = graph.getVertex(v.getId());
		if (toReturn == null) {
			toReturn = graph.addVertex(v.getId());
			toReturn.setProperty(GraphConfig.URI_PROPERTY, v.getProperty(GraphConfig.URI_PROPERTY));
		}
		return toReturn;
	}

	public static GraphJung<Graph> asGraphJung(GraphType graphType, Graph graph) {
		switch (graphType) {
		case DIRECTED_GRAPH:
			return new GraphJung<Graph>(graph);
		case UNDIRECTED_GRAPH:
			return new GraphJungUndirectedWrapper(graph);
		default:
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Throws {@link IllegalArgumentException} if the graph is null or does not have vertices.
	 * 
	 * @return the provided graph.
	 */
	public static Graph checkHasVertices(Graph graph) {
		if (hasNoVertices(graph))
			throw new IllegalArgumentException("Graph needs to have at least one vertex.");
		return graph;
	}

	public static Collection<Edge> connectedEdges(Vertex vertex, Direction direction, String... labels) {
		Collection<Edge> edges;
		final Iterable<Edge> itty = vertex.getEdges(direction);
		if (itty instanceof Collection)
			edges = (Collection<Edge>) itty;
		else {
			edges = new ArrayList<Edge>();
			for (final Edge edge : itty)
				edges.add(edge);
		}
		return edges;
	}

	public static Iterable<Edge> connectedEdges(Vertex v, GraphType graphDirection) {
		switch (graphDirection) {
		case DIRECTED_GRAPH:
			return v.getEdges(Direction.OUT);
		case UNDIRECTED_GRAPH:
			return v.getEdges(Direction.BOTH);
		default:
			throw new IllegalArgumentException("Suitable graph direction is missing: " + graphDirection);
		}
	}

	public static Set<Vertex> connectedVerticesBothDirections(Vertex vertex) {
		final Set<Vertex> vertices = new HashSet<Vertex>();
		for (final Edge edge : vertex.getEdges(Direction.BOTH)) {
			Vertex other = Graphs.oppositeVertexUnsafe(edge, vertex);
			vertices.add(other);
		}
		return vertices;
	}

	public static double edgesCountWeighted(Graph graph, EdgeWeights edgeWeights) {
		return sumWeightedEdges(graph.getEdges(), edgeWeights);
	}

	public static int edgesCount(Graph graph) {
		return CollectionUtils.iterableItemCount(graph.getEdges());
	}

	public static String edgeToString(Edge edge, EdgeWeights edgeWeights) {
		String shortUri = checkNotNull(edge).getProperty(GraphConfig.URI_PROPERTY);
		return shortUri != null ? String.format("%s (%.3f)", shortUri,
				edgeWeights.weight(edge)) : "";
	}

	public static String shortUriOfEdge(Edge edge) {
		String shortUri = edge.getProperty(GraphConfig.URI_PROPERTY);
		checkNotNull(shortUri, "Graph is corrupted. The edge %s has no uri property.", edge.getId().toString());
		return shortUri;
	}

	public static boolean hasNoVertices(Graph graph) {
		return !graph.getVertices().iterator().hasNext();
	}
	
	public static boolean hasNoEdges(Graph graph) {
		return !graph.getEdges().iterator().hasNext();
	}

	public static boolean isNodeOnPath(Vertex child, Collection<Edge> path) {
		for (Edge edge : path)
			if (child.equals(edge.getVertex(Direction.IN)) || child.equals(edge.getVertex(Direction.OUT)))
				return true;
		return false;
	}

	public static boolean isVertexInGraph(Vertex v, Graph graph) {
		String uri = Graphs.shortUriOfVertex(v);
		Vertex graphVertex = vertexByUri(graph, uri);
		return graphVertex != null;
	}

	public static Vertex oppositeVertexSafe(Edge edge, Vertex vertex) {
		Vertex in = edge.getVertex(Direction.IN);
		Vertex out = edge.getVertex(Direction.OUT);
		if (vertex.equals(out))
			return in;
		else if (vertex.equals(in))
			return out;
		else
			throw new IllegalArgumentException(String.format("Vertex %s is not part of edge %s", vertex.getId(), edge));
	}

	/**
	 * Get the other vertex of the edge. NOTE: To improve performance this method does not check if the provided vertex
	 * actually belongs to the provided edge.
	 */
	public static Vertex oppositeVertexUnsafe(Edge edge, Vertex vertex) {
		Vertex in = edge.getVertex(Direction.IN);
		if (!vertex.equals(in))
			return in;
		else
			return edge.getVertex(Direction.OUT);
	}

	public static String shortUriOfVertex(Vertex v) {
		String shortUri = v.getProperty(GraphConfig.URI_PROPERTY);
		checkNotNull(shortUri, "Graph is corrupted. The vertex %s has no uri property.", v.getId().toString());
		return shortUri;
	}

	public static Set<String> urisOfVertices(Iterable<Vertex> vertices) {
		Set<String> uris = new HashSet<>();
		for (Vertex v : vertices)
			uris.add(shortUriOfVertex(v));
		return uris;
	}

	public static Vertex vertexByUri(Graph graph, String uri) {
		String shortUri = GraphUriShortener.shorten(uri);
		List<Vertex> vertices = new LinkedList<Vertex>();
		Iterable<Vertex> verticesIter = graph.getVertices(GraphConfig.URI_PROPERTY, shortUri);
		for (Vertex v : verticesIter)
			vertices.add(v);

		if (vertices.size() == 0) {
			return null;
		}
		if (vertices.size() > 1) {
			logger.warn("There is more than one vertex with the uri {}", uri);
			for (Vertex v : vertices) {
				int inDegree = vertexDegree(v, Direction.IN);
				int outDegree = vertexDegree(v, Direction.OUT);
				logger.warn("vid: {} uri: {} inDegree: {} outDegree {} ", v.getId(),
						v.getProperty(GraphConfig.URI_PROPERTY), inDegree, outDegree);
			}
			logger.warn("");
		}

		return vertices.get(0);
	}

	@Deprecated
	public static int vertexDegree(Vertex vertex, Direction direction) {
		return CollectionUtils.iterableItemCount(vertex.getEdges(direction));
	}
	
	public static double vertexDegreeWeighted(Vertex vertex, Direction direction, EdgeWeights edgeWeights) {
		return sumWeightedEdges(vertex.getEdges(direction), edgeWeights);
	}
	
	private static double sumWeightedEdges(Iterable<Edge> edges, EdgeWeights edgeWeights) {
		double sumWeights = 0;
		for(Edge e: edges)
			sumWeights += edgeWeights.weight(e);
		return sumWeights;
	}

	public static String vertexToString(Vertex v) {
		String uri = v.getProperty(GraphConfig.URI_PROPERTY);
		return String.format("vid: %s uri: %s", v.getId().toString(), uri);
	}

	/**
	 * Converts the uris to vertices. Omits uris that cannot be found in the provided graph.
	 */
	public static Set<Vertex> verticesByUri(Graph graph, Collection<String> uris) {
		Set<Vertex> vertices = new HashSet<Vertex>();
		for (String uri : uris) {
			Vertex v = vertexByUri(graph, uri);
			if (v != null)
				vertices.add(v);
			else
				logger.warn("No vertex found for uri {}", uri);
		}
		return vertices;
	}

	public static int verticesCount(Graph graph) {
		return CollectionUtils.iterableItemCount(graph.getVertices());
	}

	@Deprecated
	public static Collection<Set<Vertex>> wordsVerticesByUri(Graph graph,
			Collection<? extends Collection<String>> wordsSensesString) {
		Collection<Set<Vertex>> wordVertices = new ArrayList<>();
		for (Collection<String> uris : wordsSensesString)
			wordVertices.add(verticesByUri(graph, uris));
		return wordVertices;
	}

	@Deprecated
	public static void addNodeAndEdgesByUriIfNonExistent(Graph graph, Collection<Edge> edges) {
		for (Edge edge : edges) {
			String outVertexUri = edge.getVertex(Direction.OUT).getProperty(GraphConfig.URI_PROPERTY).toString();
			Vertex outVertex = addVertexByUri(graph, outVertexUri);

			String inVertexUri = edge.getVertex(Direction.IN).getProperty(GraphConfig.URI_PROPERTY).toString();
			Vertex inVertex = addVertexByUri(graph, inVertexUri);

			addEdgeIfNonExistentUnsafe(graph, edge, outVertex, inVertex);
		}
	}

	/**
	 * Adds a vertex with the uri as property to the graph if it does not exist yet.
	 */
	@Deprecated
	public static Vertex addVertexByUri(Graph graph, String fullUri) {
		Vertex v = vertexByUri(graph, fullUri);
		if (v == null) {
			v = graph.addVertex(fullUri);
			v.setProperty(GraphConfig.URI_PROPERTY, fullUri);
		}
		return v;
	}

	@Deprecated
	public static void addVerticesByUrisOfVertices(Graph graph, Iterable<Vertex> vertices) {
		for (Vertex v : vertices)
			addVertexByUri(graph, v.getProperty(GraphConfig.URI_PROPERTY).toString());
	}

	@Deprecated
	public static boolean containsVertexByUri(Collection<Vertex> vertices, Vertex searchVertex) {
		for (Vertex v : vertices)
			if (Graphs.equalByUri(searchVertex, v))
				return true;
		return false;
	}

	@Deprecated
	public static boolean equalByUri(Vertex v1, Vertex v2) {
		return shortUriOfVertex(v1).equals(shortUriOfVertex(v2));
	}

	// Suppress default constructor for noninstantiability
	private Graphs() {
		throw new AssertionError();
	}
}
