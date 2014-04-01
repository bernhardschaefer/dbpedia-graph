package de.unima.dws.dbpediagraph.graph;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.tinkerpop.blueprints.*;
import com.tinkerpop.blueprints.oupls.jung.GraphJung;

import de.unima.dws.dbpediagraph.util.GraphJungUndirectedWrapper;
import de.unima.dws.dbpediagraph.weights.EdgeWeights;

/**
 * Utility class for performing various graph operations. All operations are static.
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
		final Iterable<Edge> itty = vertex.getEdges(direction, labels);
		if (itty instanceof Collection)
			edges = (Collection<Edge>) itty;
		else {
			edges = new ArrayList<Edge>();
			for (final Edge edge : itty)
				edges.add(edge);
		}
		return edges;
	}

	public static Set<Vertex> connectedVerticesBothDirections(Vertex vertex) {
		final Set<Vertex> vertices = new HashSet<Vertex>();
		for (final Edge edge : vertex.getEdges(Direction.BOTH)) {
			Vertex other = Graphs.oppositeVertexUnsafe(edge, vertex);
			vertices.add(other);
		}
		return vertices;
	}

	public static int edgesCount(Graph graph) {
		return Iterables.size(graph.getEdges());
	}

	public static double edgesCountWeighted(Graph graph, EdgeWeights edgeWeights) {
		return sumWeightedEdges(graph.getEdges(), edgeWeights);
	}

	public static String edgeToString(Edge edge, EdgeWeights edgeWeights) {
		String shortUri = checkNotNull(edge).getProperty(GraphConfig.URI_PROPERTY);
		return shortUri != null ? String.format("%s (%.3f)", shortUri, edgeWeights.transform(edge)) : "";
	}

	public static String edgeToString(Edge edge) {
		String edgeUri = checkNotNull(edge).getProperty(GraphConfig.URI_PROPERTY);
		String out = shortUriOfVertex(edge.getVertex(Direction.OUT));
		String in = shortUriOfVertex(edge.getVertex(Direction.IN));
		return String.format("%s--%s->%s", out, edgeUri, in);
	}

	public static String fullUriOfEdge(Edge edge) {
		return UriTransformer.unshorten(shortUriOfEdge(edge));
	}

	public static String shortUriOfEdge(Edge edge) {
		String shortUri = edge.getProperty(GraphConfig.URI_PROPERTY);
		checkNotNull(shortUri, "Graph is corrupted. The edge %s has no uri property.", edge.getId().toString());
		return shortUri;
	}

	public static boolean hasNoVertices(Graph graph) {
		return Iterables.isEmpty(graph.getVertices());
	}

	public static boolean hasNoEdges(Graph graph) {
		return Iterables.isEmpty(graph.getEdges());
	}

	public static boolean isNodeOnPath(Vertex child, List<Edge> path) {
		for (Edge edge : path)
			if (child.equals(edge.getVertex(Direction.IN)) || child.equals(edge.getVertex(Direction.OUT)))
				return true;
		return false;
	}

	// ----------- Vertex related methods ------------

	public static boolean vertexHasNoNeighbours(Vertex v) {
		return Iterables.isEmpty(v.getEdges(Direction.BOTH));
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

	public static String fullUriOfVertex(Vertex v) {
		return UriTransformer.unshorten(shortUriOfVertex(v));
	}

	public static String shortUriOfVertex(Vertex v) {
		String shortUri = v.getProperty(GraphConfig.URI_PROPERTY);
		checkNotNull(shortUri, "Graph is corrupted. The vertex %s has no uri property.", v.getId().toString());
		return shortUri;
	}

	public static Set<String> shortUrisOfVertices(Iterable<Vertex> vertices) {
		Set<String> uris = new HashSet<>();
		for (Vertex v : vertices)
			uris.add(shortUriOfVertex(v));
		return uris;
	}

	public static Vertex vertexByFullUri(Graph graph, String fullUri) {
		String shortUri = UriTransformer.shorten(fullUri);
		List<Vertex> vertices = Lists.newArrayList(graph.getVertices(GraphConfig.URI_PROPERTY, shortUri));
		if (vertices.size() == 0) {
			logger.debug("No vertex found for full uri {} with short uri {}", fullUri, shortUri);
			return null;
		}
		if (vertices.size() > 1) {
			throw new IllegalStateException("The graph is corrupted. There is more than one vertex with the uri "
					+ fullUri);
		}

		return vertices.get(0);
	}

	public static double vertexDegreeWeighted(Vertex vertex, Direction direction, EdgeWeights edgeWeights) {
		return sumWeightedEdges(vertex.getEdges(direction), edgeWeights);
	}

	private static double sumWeightedEdges(Iterable<Edge> edges, EdgeWeights edgeWeights) {
		double sumWeights = 0;
		for (Edge e : edges)
			sumWeights += edgeWeights.transform(e);
		return sumWeights;
	}

	public static String vertexToString(Vertex v) {
		StringBuilder builder = new StringBuilder();

		builder.append("vid: ").append(v.getId().toString());
		builder.append(" shortUri: ").append(v.getProperty(GraphConfig.URI_PROPERTY));

		List<Edge> edges = Lists.newArrayList(v.getEdges(Direction.BOTH));
		builder.append(" num edges: ").append(edges.size());

		builder.append("\nedges: ");
		for (Edge e : edges)
			builder.append(e.getProperty(GraphConfig.URI_PROPERTY)).append("--")
					.append(Graphs.oppositeVertexSafe(e, v).getProperty(GraphConfig.URI_PROPERTY)).append("\t");

		return builder.toString();
	}

	/**
	 * Converts the uris to vertices. Omits uris that cannot be found in the provided graph.
	 */
	public static Set<Vertex> verticesByFullUris(Graph graph, Collection<String> fullUris) {
		Set<Vertex> vertices = new HashSet<Vertex>();
		for (String fullUri : fullUris) {
			Vertex v = vertexByFullUri(graph, fullUri);
			if (v != null)
				vertices.add(v);
		}
		return vertices;
	}

	public static int verticesCount(Graph graph) {
		return Iterables.size(graph.getVertices());
	}

	// Suppress default constructor for noninstantiability
	private Graphs() {
		throw new AssertionError();
	}

}
