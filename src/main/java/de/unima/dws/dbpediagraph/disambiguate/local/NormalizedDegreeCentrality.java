package de.unima.dws.dbpediagraph.disambiguate.local;

import java.util.Map;

import com.tinkerpop.blueprints.*;

import de.unima.dws.dbpediagraph.disambiguate.*;
import de.unima.dws.dbpediagraph.graph.*;
import de.unima.dws.dbpediagraph.graph.GraphFactory;
import de.unima.dws.dbpediagraph.model.Sense;
import de.unima.dws.dbpediagraph.model.SurfaceForm;
import de.unima.dws.dbpediagraph.weights.EdgeWeights;
import edu.uci.ics.jung.algorithms.scoring.VertexScorer;

/**
 * Normalized Degree Centrality {@link GraphDisambiguator} that uses the vertex degree in the subgraph normalized by the
 * degree of the vertex in the global graph.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class NormalizedDegreeCentrality<T extends SurfaceForm, U extends Sense> extends
		AbstractLocalGraphDisambiguator<T, U> implements LocalGraphDisambiguator<T, U> {

	public NormalizedDegreeCentrality(GraphType graphType, EdgeWeights graphWeights) {
		super(graphType, graphWeights);
	}

	class DegreeVertexScorer implements VertexScorer<Vertex, Double> {
		@Override
		public Double getVertexScore(Vertex v) {
			// TODO should Direction.IN be used for directed graphs?
			double subgraphDegree = Graphs.vertexDegreeWeighted(v, Direction.BOTH, edgeWeights);

			Vertex vDBpediaGraph = GraphFactory.getDBpediaGraph().getVertex(v.getId());
			double totalDegree = Graphs.vertexDegreeWeighted(vDBpediaGraph, Direction.BOTH, edgeWeights);

			double centrality = subgraphDegree / totalDegree;
			return centrality;
		}

	}

	@Override
	protected VertexScorer<Vertex, Double> getVertexScorer(Graph subgraph, Map<Vertex, Double> vertexPriors) {
		return new DegreeVertexScorer();
	}
}
