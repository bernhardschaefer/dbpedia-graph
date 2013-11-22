package de.unima.dws.dbpediagraph.graphdb.disambiguate;

import java.util.*;
import java.util.Map.Entry;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.model.*;
import de.unima.dws.dbpediagraph.graphdb.subgraph.*;

/**
 * Skeleton class which eases the implementation of {@link GraphDisambiguator}. Subclasses only need to implement
 * {@link #globalConnectivityMeasure(Map, Graph)}.
 * 
 * @author Bernhard Schäfer
 * 
 */
public abstract class AbstractGlobalGraphDisambiguator<T extends SurfaceForm, U extends Sense> implements
		GlobalGraphDisambiguator<T, U> {
	protected final SubgraphConstructionSettings subgraphConstructionSettings;
	private final ModelFactory<T, U> factory;

	public AbstractGlobalGraphDisambiguator(SubgraphConstructionSettings subgraphConstructionSettings,
			ModelFactory<T, U> factory) {
		this.subgraphConstructionSettings = subgraphConstructionSettings;
		this.factory = factory;
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

		final Set<Vertex> allSensesVertices = Sets.newHashSet(Iterables.concat(ModelTransformer
				.wordsVerticesFromSenses(subgraph, surfaceFormsSenses)));
		ConnectivityMeasureFunction<T, U> scoreFunction = new ConnectivityMeasureFunction<T, U>() {
			@Override
			public double getMeasure(Map<T, U> assignments, Graph subgraph) {
				return globalConnectivityMeasure(assignments, subgraph, allSensesVertices);
			}
		};
		Searcher<T, U> searcher = new SimulatedAnnealing<T, U>(new AimaScheduler(1000), scoreFunction);

		Map<T, U> finalAssignment = searcher.search(surfaceFormsSenses, subgraph);

		double finalScore = scoreFunction.getMeasure(finalAssignment, subgraph);

		return wrap(finalAssignment, finalScore);
	}

	private List<SurfaceFormSenseScore<T, U>> wrap(Map<T, U> result, double score) {
		List<SurfaceFormSenseScore<T, U>> scoresResults = new ArrayList<>();
		for (Entry<T, U> entry : result.entrySet()) {
			scoresResults.add(factory.newSurfaceFormSenseScore(entry.getKey(), entry.getValue(), score));
		}
		return scoresResults;
	}

	@Override
	public double globalConnectivityMeasure(Collection<Vertex> assigments, Graph subgraph, Set<Vertex> sensesVertices) {
		SubgraphConstruction sensegraphConstruction = SubgraphConstructionFactory.newSubgraphConstruction(subgraph,
				subgraphConstructionSettings);
		Graph sensegraph = sensegraphConstruction.createSubSubgraph(assigments, sensesVertices);
		double score = globalConnectivityMeasure(sensegraph);
		sensegraph.shutdown();
		return score;
	}

	public abstract double globalConnectivityMeasure(Graph sensegraph);

	@Override
	public double globalConnectivityMeasure(Map<T, U> surfaceFormSenseAssigments, Graph subgraph,
			Set<Vertex> sensesVertices) {
		Collection<Vertex> assignments = ModelTransformer.verticesFromSenses(subgraph,
				surfaceFormSenseAssigments.values());
		return globalConnectivityMeasure(assignments, subgraph, sensesVertices);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
