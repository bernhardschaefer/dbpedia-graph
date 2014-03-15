package de.unima.dws.dbpediagraph.disambiguate;

import java.util.*;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graph.GraphType;
import de.unima.dws.dbpediagraph.model.*;
import de.unima.dws.dbpediagraph.search.*;
import de.unima.dws.dbpediagraph.subgraph.SubgraphConstructionFactory;
import de.unima.dws.dbpediagraph.subgraph.SubgraphConstructionSettings;
import de.unima.dws.dbpediagraph.weights.EdgeWeights;

/**
 * Skeleton class which eases the implementation of {@link GraphDisambiguator}. Subclasses only need to implement
 * {@link #globalConnectivityMeasure(Map, Graph)}.
 * 
 * @author Bernhard Schäfer
 * 
 */
public abstract class AbstractGlobalGraphDisambiguator<T extends SurfaceForm, U extends Sense> implements
		GlobalGraphDisambiguator<T, U> {
	private static final Logger logger = LoggerFactory.getLogger(AbstractGlobalGraphDisambiguator.class);

	private final SubgraphConstructionSettings assignmentGraphConstrSettings;

	protected final EdgeWeights edgeWeights;
	protected GraphType graphType;

	public AbstractGlobalGraphDisambiguator(GraphType graphType, EdgeWeights edgeWeights) {
		this.assignmentGraphConstrSettings = new SubgraphConstructionSettings.Builder().graphType(graphType)
				.maxDistance(Integer.MAX_VALUE).persistSubgraph(false).build();
		this.graphType = graphType;
		this.edgeWeights = edgeWeights;
	}

	@Override
	public Map<T, List<SurfaceFormSenseScore<T, U>>> bestK(Map<T, List<U>> surfaceFormsSenses, Graph subgraph, int k) {
		// TODO think about how to simulate this
		throw new UnsupportedOperationException("bestK not supported for global disambiguators");
	}

	@Override
	public List<SurfaceFormSenseScore<T, U>> disambiguate(final Map<T, List<U>> surfaceFormsSenses, Graph subgraph) {
		// TODO check optaplanner:
		// http://docs.jboss.org/drools/release/latest/optaplanner-docs/html_single/index.html#optimizationAlgorithms
		logger.info("Using disambiguator {}", this);

		final Set<Vertex> allSensesVertices = Sets.newHashSet(Iterables.concat(ModelToVertex
				.verticesFromSurfaceFormSenses(subgraph, surfaceFormsSenses, true)));
		ConnectivityMeasureFunction<T, U> scoreFunction = new ConnectivityMeasureFunction<T, U>() {
			@Override
			public double getMeasure(Map<T, U> assignments, Graph subgraph) {
				return globalConnectivityMeasure(assignments, subgraph, allSensesVertices);
			}
		};
		int maxIterations = 1000;
		Searcher searcher = SearcherFactory.newDefaultSearcher(maxIterations);

		Map<T, U> finalAssignment = searcher.search(surfaceFormsSenses, subgraph, scoreFunction);

		double finalScore = scoreFunction.getMeasure(finalAssignment, subgraph);

		return wrap(finalAssignment, finalScore);
	}

	private List<SurfaceFormSenseScore<T, U>> wrap(Map<T, U> result, double score) {
		List<SurfaceFormSenseScore<T, U>> scoresResults = new ArrayList<>();
		for (Entry<T, U> entry : result.entrySet()) {
			scoresResults.add(new SurfaceFormSenseScore<T, U>(entry.getKey(), entry.getValue(), score));
		}
		return scoresResults;
	}

	@Override
	public double globalConnectivityMeasure(Collection<Vertex> assigments, Graph subgraph, Set<Vertex> sensesVertices) {
		Graph sensegraph = SubgraphConstructionFactory.newSubgraphConstruction(subgraph, assignmentGraphConstrSettings)
				.createSubSubgraph(assigments, sensesVertices);
		double score = globalConnectivityMeasure(sensegraph);
		sensegraph.shutdown();
		return score;
	}

	public abstract double globalConnectivityMeasure(Graph sensegraph);

	@Override
	public double globalConnectivityMeasure(Map<T, U> surfaceFormSenseAssigments, Graph subgraph,
			Set<Vertex> sensesVertices) {
		Collection<Vertex> assignments = ModelToVertex
				.verticesFromSenses(subgraph, surfaceFormSenseAssigments.values());
		return globalConnectivityMeasure(assignments, subgraph, sensesVertices);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[ graphType: " + graphType + " ]";
	}

}
