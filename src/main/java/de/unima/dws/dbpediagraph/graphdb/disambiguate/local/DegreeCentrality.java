package de.unima.dws.dbpediagraph.graphdb.disambiguate.local;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.GraphType;
import de.unima.dws.dbpediagraph.graphdb.Graphs;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.AbstractLocalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.GraphDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.LocalGraphDisambiguator;
import edu.uci.ics.jung.algorithms.scoring.VertexScorer;

/**
 * Degree Centrality {@link GraphDisambiguator} that only takes into account the degree of edges in the subgraph.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class DegreeCentrality extends AbstractLocalGraphDisambiguator implements LocalGraphDisambiguator {
	class DegreeVertexScorer implements VertexScorer<Vertex, Double> {
		private final int verticesCount;

		public DegreeVertexScorer(Graph subgraph) {
			this.verticesCount = Graphs.numberOfVertices(subgraph);
		}

		@Override
		public Double getVertexScore(Vertex v) {
			double degree = Graphs.vertexDegree(v, direction);
			double centrality = degree / (verticesCount - 1);
			return centrality;
		}

	}

	public static final DegreeCentrality IN_AND_OUT_DEGREE = new DegreeCentrality(Direction.BOTH);
	public static final DegreeCentrality IN_DEGREE = new DegreeCentrality(Direction.IN);
	public static final DegreeCentrality OUT_DEGREE = new DegreeCentrality(Direction.OUT);

	public static DegreeCentrality forGraphType(GraphType graphType) {
		switch (graphType) {
		case DIRECTED_GRAPH:
			return IN_DEGREE;
		case UNDIRECTED_GRAPH:
			return IN_AND_OUT_DEGREE;
		default:
			throw new IllegalArgumentException();
		}
	}

	private final Direction direction;

	/**
	 * The direction of edges to be used for degree measurement. E.g. Direction.BOTH means that both in- and out edges
	 * are considered for the degree calculation, whereas Direction.IN refers to the indegree of an edge.
	 */
	private DegreeCentrality(Direction direction) {
		this.direction = direction;
	}

	@Override
	protected VertexScorer<Vertex, Double> getVertexScorer(Graph subgraph) {
		return new DegreeVertexScorer(subgraph);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " (direction: " + direction + " )";
	}

}
