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
 * Global Degree Centrality {@link GraphDisambiguator} uses the vertex degree of the global (DBpedia) graph normalized
 * by the number of vertices in the global graph.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class GlobalDegreeCentrality<T extends SurfaceForm, U extends Sense> extends
		AbstractLocalGraphDisambiguator<T, U> implements LocalGraphDisambiguator<T, U> {

	private static final double globalVerticesCount = Graphs.verticesCount(GraphFactory.getDBpediaGraph());

	public GlobalDegreeCentrality(GraphType graphType, EdgeWeights graphWeights) {
		super(graphType, graphWeights);
	}

	class DegreeVertexScorer implements VertexScorer<Vertex, Double> {
		@Override
		public Double getVertexScore(Vertex v) {
			// TODO should Direction.IN be used for directed graphs?
			Vertex vDBpediaGraph = GraphFactory.getDBpediaGraph().getVertex(v.getId());
			double totalDegree = Graphs.vertexDegreeWeighted(vDBpediaGraph, Direction.BOTH, edgeWeights);

			double centrality = totalDegree / globalVerticesCount;
			return centrality;
		}

	}

	@Override
	protected VertexScorer<Vertex, Double> getVertexScorer(Graph subgraph, Map<Vertex, Double> vertexPriors) {
		return new DegreeVertexScorer();
	}
}
