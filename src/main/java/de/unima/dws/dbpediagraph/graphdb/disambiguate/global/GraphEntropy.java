package de.unima.dws.dbpediagraph.graphdb.disambiguate.global;

import java.util.Collection;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.GraphUtil;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.AbstractGlobalDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.ConnectivityMeasure;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstruction;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstructionNavigliOld;

public class GraphEntropy extends AbstractGlobalDisambiguator {

	@Override
	public ConnectivityMeasure getType() {
		return ConnectivityMeasure.GraphEntropy;
	}

	@Override
	public Double globalConnectivityMeasure(Collection<String> senseAssignments, Graph subgraph) {

		SubgraphConstruction subgraphConstruction = new SubgraphConstructionNavigliOld(subgraph, 10);
		subgraphConstruction.setGraph(subgraph);
		Graph sensegraph = subgraphConstruction.createSubgraph(GraphUtil.getVerticesByUri(subgraph, senseAssignments));

		int totalEdges = GraphUtil.getNumberOfEdges(sensegraph);

		double graphEntropy = 0;

		for (Vertex vertex : sensegraph.getVertices()) {
			double degree = GraphUtil.getEdgesOfVertex(vertex, Direction.BOTH).size();
			double vertexProbability = degree / (2.0 * totalEdges);
			graphEntropy += vertexProbability * Math.log10(vertexProbability);
		}
		graphEntropy *= -1;

		sensegraph.shutdown();
		return graphEntropy;
	}

}
