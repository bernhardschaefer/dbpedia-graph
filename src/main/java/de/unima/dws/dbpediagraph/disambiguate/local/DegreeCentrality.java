package de.unima.dws.dbpediagraph.disambiguate.local;

import java.util.Map;

import com.tinkerpop.blueprints.*;

import de.unima.dws.dbpediagraph.disambiguate.*;
import de.unima.dws.dbpediagraph.graph.GraphType;
import de.unima.dws.dbpediagraph.graph.Graphs;
import de.unima.dws.dbpediagraph.model.Sense;
import de.unima.dws.dbpediagraph.model.SurfaceForm;
import de.unima.dws.dbpediagraph.weights.EdgeWeights;
import edu.uci.ics.jung.algorithms.scoring.VertexScorer;

/**
 * Degree Centrality {@link GraphDisambiguator} that only takes into account the degree of edges in the subgraph.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class DegreeCentrality<T extends SurfaceForm, U extends Sense> extends AbstractLocalGraphDisambiguator<T, U>
		implements LocalGraphDisambiguator<T, U> {

	public DegreeCentrality(GraphType graphType, EdgeWeights graphWeights) {
		super(graphType, graphWeights);
	}

	class DegreeVertexScorer implements VertexScorer<Vertex, Double> {
		private final int verticesCount;

		public DegreeVertexScorer(Graph subgraph) {
			this.verticesCount = Graphs.verticesCount(subgraph);
		}

		@Override
		public Double getVertexScore(Vertex v) {
			if (verticesCount == 1) // shortcut to prevent division by zero NaN
				return 0.0;
			// TODO should Direction.IN be used for directed graphs?
			double degree = Graphs.vertexDegreeWeighted(v, Direction.BOTH, edgeWeights);
			double centrality = degree / (verticesCount - 1);
			return centrality;
		}

	}

	@Override
	protected VertexScorer<Vertex, Double> getVertexScorer(Graph subgraph, Map<Vertex, Double> vertexPriors) {
		return new DegreeVertexScorer(subgraph);
	}

}
