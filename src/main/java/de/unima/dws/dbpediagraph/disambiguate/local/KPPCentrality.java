package de.unima.dws.dbpediagraph.disambiguate.local;

import java.util.Map;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.oupls.jung.GraphJung;

import de.unima.dws.dbpediagraph.disambiguate.AbstractLocalGraphDisambiguator;
import de.unima.dws.dbpediagraph.disambiguate.LocalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graph.GraphType;
import de.unima.dws.dbpediagraph.graph.Graphs;
import de.unima.dws.dbpediagraph.model.Sense;
import de.unima.dws.dbpediagraph.model.SurfaceForm;
import de.unima.dws.dbpediagraph.weights.EdgeWeights;
import edu.uci.ics.jung.algorithms.scoring.VertexScorer;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraDistance;
import edu.uci.ics.jung.algorithms.shortestpath.Distance;

/**
 * @author Bernhard Schäfer
 */
public class KPPCentrality<T extends SurfaceForm, U extends Sense> extends AbstractLocalGraphDisambiguator<T, U>
		implements LocalGraphDisambiguator<T, U> {

	public KPPCentrality(GraphType graphType, EdgeWeights graphWeights) {
		super(graphType, graphWeights);
	}

	class KPPVertexScorer implements VertexScorer<Vertex, Double> {
		private final Distance<Vertex> distances;
		private final int verticesCount;
		private final Graph subgraph;

		public KPPVertexScorer(Graph subgraph) {
			this.subgraph = subgraph;
			GraphJung<Graph> graphJung = Graphs.asGraphJung(graphType, subgraph);
			// distances = new UnweightedShortestPath<>(graphJung);
			distances = new DijkstraDistance<>(graphJung, edgeWeights);
			verticesCount = Graphs.verticesCount(subgraph);
		}

		@Override
		public Double getVertexScore(Vertex v) {
			if (verticesCount == 1) // shortcut to prevent division by zero NaN
				return 0.0;
			Map<Vertex, Number> distancesFromVertex = distances.getDistanceMap(v);
			double sumInverseShortestDistances = 0;
			for (Vertex otherVertex : subgraph.getVertices()) {
				if (otherVertex.equals(v))
					continue;
				Number distance = distancesFromVertex.get(otherVertex);
				double inverseShortestDistance;
				if (distance == null || distance.doubleValue() == 0)
					// Navigli & Lapata: if there is no path, the distance is the constant 1/(numberOfVertices-1)
					// inverseShortestDistance = 1.0 / (verticesCount - 1);

					// we wan't a score of 0 for unconnected vertices
					inverseShortestDistance = 0.0;
				else
					inverseShortestDistance = 1.0 / distance.doubleValue();
				sumInverseShortestDistances += inverseShortestDistance;
			}

			double score = sumInverseShortestDistances / (verticesCount - 1);
			return score;
		}

	}

	@Override
	protected VertexScorer<Vertex, Double> getVertexScorer(Graph subgraph, Map<Vertex, Double> vertexPriors) {
		return new KPPVertexScorer(subgraph);
	}

}