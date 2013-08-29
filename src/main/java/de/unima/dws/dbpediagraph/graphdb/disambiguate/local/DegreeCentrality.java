package de.unima.dws.dbpediagraph.graphdb.disambiguate.local;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.GraphUtil;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.LocalConnectivityMeasure;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.LocalDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.WeightedUri;

/**
 * Degree Centrality Disambiguator that only takes into account the degree of edges in the subgraph.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class DegreeCentrality implements LocalDisambiguator {
	private final Direction direction;

	/**
	 * The direction of edges to be used for degree measurement. E.g. Direction.BOTH means that both in- and out edges
	 * are considered for the degree calculation, whereas Direction.IN refers to the indegree of an edge.
	 */
	public DegreeCentrality(Direction direction) {
		this.direction = direction;
	}

	@Override
	public List<WeightedUri> disambiguate(Collection<String> uris, Graph subgraph) {
		int numberOfVertices = GraphUtil.getNumberOfVertices(subgraph);

		List<WeightedUri> weightedUris = new LinkedList<>();
		for (String uri : uris) {
			Vertex v = GraphUtil.getVertexByUri(subgraph, uri);
			double inDegree = GraphUtil.getEdgesOfVertex(v, direction).size();
			double centrality = inDegree / (numberOfVertices - 1);
			weightedUris.add(new WeightedUri(uri, centrality));
		}

		Collections.sort(weightedUris);
		Collections.reverse(weightedUris);

		return weightedUris;
	}

	@Override
	public LocalConnectivityMeasure getType() {
		return LocalConnectivityMeasure.Degree;
	}

}
