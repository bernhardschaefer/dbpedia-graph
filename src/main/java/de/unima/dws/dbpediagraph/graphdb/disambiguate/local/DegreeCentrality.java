package de.unima.dws.dbpediagraph.graphdb.disambiguate.local;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.Graphs;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.AbstractLocalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.GraphDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.LocalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.model.ModelFactory;
import de.unima.dws.dbpediagraph.graphdb.model.Sense;
import de.unima.dws.dbpediagraph.graphdb.model.SurfaceForm;
import edu.uci.ics.jung.algorithms.scoring.VertexScorer;

/**
 * Degree Centrality {@link GraphDisambiguator} that only takes into account the
 * degree of edges in the subgraph.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class DegreeCentrality<T extends SurfaceForm, U extends Sense> extends AbstractLocalGraphDisambiguator<T, U>
		implements LocalGraphDisambiguator<T, U> {
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

	private final Direction direction;

	/**
	 * The direction of edges to be used for degree measurement. E.g.
	 * Direction.BOTH means that both in- and out edges are considered for the
	 * degree calculation, whereas Direction.IN refers to the indegree of an
	 * edge.
	 */
	public DegreeCentrality(Direction direction, ModelFactory<T, U> factory) {
		super(factory);
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
