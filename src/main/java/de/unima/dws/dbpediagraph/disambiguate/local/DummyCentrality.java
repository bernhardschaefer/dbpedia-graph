package de.unima.dws.dbpediagraph.disambiguate.local;

import com.tinkerpop.blueprints.*;

import de.unima.dws.dbpediagraph.disambiguate.*;
import de.unima.dws.dbpediagraph.graph.GraphType;
import de.unima.dws.dbpediagraph.model.Sense;
import de.unima.dws.dbpediagraph.model.SurfaceForm;
import de.unima.dws.dbpediagraph.weights.EdgeWeights;
import edu.uci.ics.jung.algorithms.scoring.VertexScorer;

/**
 * Dummy Centrality {@link GraphDisambiguator} that assigns a score of zero to each candidate.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class DummyCentrality<T extends SurfaceForm, U extends Sense> extends AbstractLocalGraphDisambiguator<T, U>
		implements LocalGraphDisambiguator<T, U> {

	public DummyCentrality(GraphType graphType, EdgeWeights graphWeights) {
		super(graphType, graphWeights);
	}

	private static final VertexScorer<Vertex, Double> DUMMY_SCORER = new VertexScorer<Vertex, Double>() {
		@Override
		public Double getVertexScore(Vertex v) {
			return 0d;
		}
	};

	@Override
	protected VertexScorer<Vertex, Double> getVertexScorer(Graph subgraph) {
		return DUMMY_SCORER;
	}

}
