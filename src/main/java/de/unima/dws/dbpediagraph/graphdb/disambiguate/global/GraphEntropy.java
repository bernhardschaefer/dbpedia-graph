package de.unima.dws.dbpediagraph.graphdb.disambiguate.global;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.Graphs;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.AbstractGlobalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.GlobalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.model.Sense;
import de.unima.dws.dbpediagraph.graphdb.model.SurfaceForm;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstructionSettings;

/**
 * Graph Entropy global connectivity measure implemented as described in Navigli&Lapata (2010).
 * 
 * @author Bernhard Schäfer
 * 
 */
public class GraphEntropy<T extends SurfaceForm, U extends Sense> extends AbstractGlobalGraphDisambiguator<T, U>
		implements GlobalGraphDisambiguator<T, U> {

	public GraphEntropy(SubgraphConstructionSettings settings) {
		super(settings);
	}

	@Override
	public double globalConnectivityMeasure(Graph sensegraph) {
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

}
