package de.unima.dws.dbpediagraph.graphdb.disambiguate.global;

import com.tinkerpop.blueprints.*;

import de.unima.dws.dbpediagraph.graphdb.Graphs;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.AbstractGlobalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.GlobalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.model.*;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstructionSettings;

/**
 * Graph Entropy global connectivity measure implemented as described in Navigli&Lapata (2010).
 * 
 * @author Bernhard Schäfer
 * 
 */
public class GraphEntropy<T extends SurfaceForm, U extends Sense> extends AbstractGlobalGraphDisambiguator<T, U>
		implements GlobalGraphDisambiguator<T, U> {

	public GraphEntropy(SubgraphConstructionSettings settings, ModelFactory<T, U> factory) {
		super(settings, factory);
	}

	@Override
	public double globalConnectivityMeasure(Graph sensegraph) {
		Graphs.checkHasVertices(sensegraph);

		int totalVertices = Graphs.verticesCount(sensegraph);
		int totalEdges = Graphs.edgesCount(sensegraph);

		if (totalEdges == 0)
			return 0; // shortcut

		double graphEntropy = 0;

		for (Vertex vertex : sensegraph.getVertices()) {
			double degree = Graphs.vertexDegree(vertex, Direction.BOTH);
			if (degree != 0) {
				double vertexProbability = degree / (2.0 * totalEdges);
				graphEntropy += vertexProbability * Math.log(vertexProbability);
			}
		}
		graphEntropy *= -1;

		graphEntropy /= Math.log(totalVertices);

		return graphEntropy;
	}

}
