package de.unima.dws.dbpediagraph.disambiguate.local;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.disambiguate.*;
import de.unima.dws.dbpediagraph.graph.GraphType;
import de.unima.dws.dbpediagraph.graph.Graphs;
import de.unima.dws.dbpediagraph.model.*;
import edu.uci.ics.jung.algorithms.scoring.VertexScorer;

/**
 * Degree Centrality {@link GraphDisambiguator} that only takes into account the degree of edges in the subgraph.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class DegreeCentrality<T extends SurfaceForm, U extends Sense> extends AbstractLocalGraphDisambiguator<T, U>
		implements LocalGraphDisambiguator<T, U> {
	class DegreeVertexScorer implements VertexScorer<Vertex, Double> {
		private final int verticesCount;

		public DegreeVertexScorer(Graph subgraph) {
			this.verticesCount = Graphs.verticesCount(subgraph);
		}

		@Override
		public Double getVertexScore(Vertex v) {
			double degree = Graphs.vertexDegree(v, graphType.getDirection());
			double centrality = degree / (verticesCount - 1);
			return centrality;
		}

	}

	public DegreeCentrality(GraphType graphType, ModelFactory<T, U> factory) {
		super(graphType, factory);
	}

	@Override
	protected VertexScorer<Vertex, Double> getVertexScorer(Graph subgraph) {
		return new DegreeVertexScorer(subgraph);
	}

}