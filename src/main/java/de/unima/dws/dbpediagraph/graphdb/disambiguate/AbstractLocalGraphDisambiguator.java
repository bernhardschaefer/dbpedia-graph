package de.unima.dws.dbpediagraph.graphdb.disambiguate;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.Graphs;
import de.unima.dws.dbpediagraph.graphdb.model.ModelFactory;
import de.unima.dws.dbpediagraph.graphdb.model.ModelTransformer;
import de.unima.dws.dbpediagraph.graphdb.model.Sense;
import de.unima.dws.dbpediagraph.graphdb.model.SurfaceForm;
import de.unima.dws.dbpediagraph.graphdb.model.SurfaceFormSenseScore;
import de.unima.dws.dbpediagraph.graphdb.model.SurfaceFormSenses;
import edu.uci.ics.jung.algorithms.scoring.VertexScorer;

/**
 * Skeleton class which eases the implementation of {@link GraphDisambiguator}.
 * Subclasses only need to implement {@link #getVertexScorer(Graph)}.
 * 
 * @author Bernhard Schäfer
 * 
 */
public abstract class AbstractLocalGraphDisambiguator<T extends SurfaceForm, U extends Sense> implements
		GraphDisambiguator<T, U> {

	protected ModelFactory<T, U> factory;

	public AbstractLocalGraphDisambiguator(ModelFactory<T, U> factory) {
		this.factory = factory;
	}

	@Override
	public List<SurfaceFormSenseScore<T, U>> disambiguate(
			Collection<? extends SurfaceFormSenses<T, U>> surfaceFormsSenses, Graph subgraph) {
		VertexScorer<Vertex, Double> vertexScorer = getVertexScorer(subgraph);
		List<SurfaceFormSenseScore<T, U>> senseScores = ModelTransformer.initializeScores(surfaceFormsSenses, factory);
		for (SurfaceFormSenseScore<T, U> senseScore : senseScores) {
			Vertex v = Graphs.vertexByUri(subgraph, senseScore.sense().fullUri());
			double score = (v == null) ? -1 : vertexScorer.getVertexScore(v);
			senseScore.setScore(score);
		}
		Collections.sort(senseScores);
		Collections.reverse(senseScores);
		return senseScores;
	}

	/**
	 * This method is called in {@link #disambiguate(Collection, Graph)} to
	 * retrieve a score for each vertex corresponding to a sense.
	 * 
	 * @param subgraph
	 *            the subgraph that the vertices are contained in
	 * @return a {@link VertexScorer} implementation
	 */
	protected abstract VertexScorer<Vertex, Double> getVertexScorer(Graph subgraph);

}
