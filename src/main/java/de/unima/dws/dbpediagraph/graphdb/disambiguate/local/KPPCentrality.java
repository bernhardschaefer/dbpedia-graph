package de.unima.dws.dbpediagraph.graphdb.disambiguate.local;

import java.util.Map;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.oupls.jung.GraphJung;

import de.unima.dws.dbpediagraph.graphdb.GraphType;
import de.unima.dws.dbpediagraph.graphdb.Graphs;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.AbstractLocalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.LocalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.model.ModelFactory;
import de.unima.dws.dbpediagraph.graphdb.model.Sense;
import de.unima.dws.dbpediagraph.graphdb.model.SurfaceForm;
import edu.uci.ics.jung.algorithms.scoring.VertexScorer;
import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;

/**
 * @author Bernhard Schäfer
 */
public class KPPCentrality<T extends SurfaceForm, U extends Sense> extends AbstractLocalGraphDisambiguator<T, U>
		implements LocalGraphDisambiguator<T, U> {

	class KPPVertexScorer implements VertexScorer<Vertex, Double> {
		private final UnweightedShortestPath<Vertex, Edge> distances;
		private final int numberOfVertices;
		private final Graph subgraph;

		public KPPVertexScorer(Graph subgraph) {
			this.subgraph = subgraph;
			GraphJung<Graph> graphJung = Graphs.asGraphJung(graphType, subgraph);
			distances = new UnweightedShortestPath<>(graphJung);
			numberOfVertices = Graphs.numberOfVertices(subgraph);
		}

		@Override
		public Double getVertexScore(Vertex v) {
			Map<Vertex, Number> distancesFromVertex = distances.getDistanceMap(v);
			double sumInverseShortestDistances = 0;
			for (Vertex otherVertex : subgraph.getVertices()) {
				if (otherVertex.equals(v))
					continue;
				Number distance = distancesFromVertex.get(otherVertex);
				double inverseShortestDistance;
				if (distance == null || distance.doubleValue() == 0)
					// if there is no path, the distance is the constant 1/(numberOfVertices-1)
					inverseShortestDistance = 1.0 / (numberOfVertices - 1);
				else
					inverseShortestDistance = 1.0 / distance.doubleValue();
				sumInverseShortestDistances += inverseShortestDistance;
			}

			double score = sumInverseShortestDistances / (numberOfVertices - 1);
			return score;
		}

	}

	private final GraphType graphType;

	public KPPCentrality(GraphType graphType, ModelFactory<T, U> factory) {
		super(factory);
		this.graphType = graphType;
	}

	@Override
	protected VertexScorer<Vertex, Double> getVertexScorer(Graph subgraph) {
		return new KPPVertexScorer(subgraph);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " (graphType: " + graphType + " )";
	}

}
