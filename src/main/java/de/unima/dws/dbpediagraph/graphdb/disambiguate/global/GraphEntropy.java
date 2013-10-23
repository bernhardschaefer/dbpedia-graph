package de.unima.dws.dbpediagraph.graphdb.disambiguate.global;

import java.util.Collection;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.Graphs;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.AbstractGlobalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.GlobalGraphDisambiguator;

/**
 * Graph Entropy global connectivity measure implemented as described in Navigli&Lapata (2010).
 * 
 * @author Bernhard Schäfer
 * 
 */
public class GraphEntropy extends AbstractGlobalGraphDisambiguator implements GlobalGraphDisambiguator {

	@Override
	public Double globalConnectivityMeasure(Collection<String> senseAssignments, Graph sensegraph) {

		int totalVertices = Graphs.numberOfVertices(sensegraph);
		int totalEdges = Graphs.numberOfEdges(sensegraph);

		double graphEntropy = 0;

		for (Vertex vertex : sensegraph.getVertices()) {
			double degree = Graphs.vertexDegree(vertex, Direction.BOTH);
			double vertexProbability = degree / (2.0 * totalEdges);
			graphEntropy += vertexProbability * Math.log(vertexProbability);
		}
		graphEntropy *= -1;

		graphEntropy /= Math.log(totalVertices);

		return graphEntropy;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

}
