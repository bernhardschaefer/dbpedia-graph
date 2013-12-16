package de.unima.dws.dbpediagraph.disambiguate.global;

import com.tinkerpop.blueprints.*;

import de.unima.dws.dbpediagraph.disambiguate.AbstractGlobalGraphDisambiguator;
import de.unima.dws.dbpediagraph.disambiguate.GlobalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graph.GraphType;
import de.unima.dws.dbpediagraph.graph.Graphs;
import de.unima.dws.dbpediagraph.model.Sense;
import de.unima.dws.dbpediagraph.model.SurfaceForm;
import de.unima.dws.dbpediagraph.weights.EdgeWeights;

/**
 * Graph Entropy global connectivity measure implemented as described in Navigli&Lapata (2010).
 * 
 * @author Bernhard Schäfer
 * 
 */
public class GraphEntropy<T extends SurfaceForm, U extends Sense> extends AbstractGlobalGraphDisambiguator<T, U>
		implements GlobalGraphDisambiguator<T, U> {

	public GraphEntropy(GraphType graphType, EdgeWeights edgeWeights) {
		super(graphType, edgeWeights);
	}

	@Override
	public double globalConnectivityMeasure(Graph sensegraph) {
		Graphs.checkHasVertices(sensegraph);

		int totalVertices = Graphs.verticesCount(sensegraph);
		double totalEdgesWeighted = Graphs.edgesCountWeighted(sensegraph, edgeWeights);

		if (totalEdgesWeighted == 0)
			return 0; // shortcut

		double graphEntropy = 0;

		for (Vertex vertex : sensegraph.getVertices()) {
			double degree = Graphs.vertexDegreeWeighted(vertex, Direction.BOTH, edgeWeights);
			if (degree != 0) {
				double vertexProbability = degree / (2.0 * totalEdgesWeighted);
				graphEntropy += vertexProbability * Math.log(vertexProbability);
			}
		}
		graphEntropy *= -1;

		graphEntropy /= Math.log(totalVertices);

		return graphEntropy;
	}

}
