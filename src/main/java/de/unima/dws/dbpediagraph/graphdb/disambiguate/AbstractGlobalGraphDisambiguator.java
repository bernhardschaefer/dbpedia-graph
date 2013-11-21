package de.unima.dws.dbpediagraph.graphdb.disambiguate;

import java.util.*;
import java.util.Map.Entry;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.disambiguate.SimulatedAnnealing.ScoreFunction;
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
		// TODO check ai book code: https://code.google.com/p/aima-java/

		ScoreFunction<T, U> scoreFunction = new ScoreFunction<T, U>() {
			@Override
			public double getScore(Map<T, U> assignments, Graph subgraph) {
				return globalConnectivityMeasure(assignments, subgraph, surfaceFormsSenses);
			}
		};

		// The number of iterations u for SA was set to 5,000
		int maxU = 5000;

		// and, following [56], the constant T, initially set to 1.0, was reset to T := 0.9 * T after the u iterations.
		double initialTemperature = 1.0;
		Searcher<T, U> searcher = new SimulatedAnnealing<T, U>(maxU, initialTemperature, new Random());
		Map<T, U> result = searcher.search(surfaceFormsSenses, subgraph, scoreFunction);
		double score = searcher.getScore();

		return wrap(result, score);

		// private static class ConnectivityHeuristicFunction implements HeuristicFunction {
		// @Override
		// public double h(Object state) {
		// return 0;
		// }
		// };
		//
		// Object initialState = null;
		//
		// ActionsFunction actionsFunction = new ActionsFunction() {
		// @Override
		// public Set<Action> actions(Object s) {
		// return null;
		// }
		// };
		// ResultFunction resultFunction = new ResultFunction() {
		// @Override
		// public Object result(Object s, Action a) {
		// return null;
		// }
		// };
		// GoalTest goalTest = new GoalTest() {
		// @Override
		// public boolean isGoalState(Object state) {
		// return false;
		// }
		// };
		// Problem p = new Problem(initialState, actionsFunction, resultFunction, goalTest);
		// HeuristicFunction hf = new ConnectivityHeuristicFunction();
		// SimulatedAnnealingSearch sas = new SimulatedAnnealingSearch(hf);
		// try {
		// sas.search(p);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

	}

	private List<SurfaceFormSenseScore<T, U>> wrap(Map<T, U> result, double score) {
		List<SurfaceFormSenseScore<T, U>> scoresResults = new ArrayList<>();
		for (Entry<T, U> entry : result.entrySet()) {
			scoresResults.add(factory.newSurfaceFormSenseScore(entry.getKey(), entry.getValue(), score));
		}
		return scoresResults;
	}

	@Override
	public double globalConnectivityMeasure(Collection<Vertex> assigments, Graph subgraph,
			Collection<Set<Vertex>> surfaceFormsVertices) {
		SubgraphConstruction sensegraphConstruction = SubgraphConstructionFactory.newSubgraphConstruction(subgraph,
				subgraphConstructionSettings);
		Set<Vertex> allSensesVertices = Sets.newHashSet(Iterables.concat(surfaceFormsVertices));
		// CollectionUtils.combine(surfaceFormsVertices)
		Graph sensegraph = sensegraphConstruction.createSubSubgraph(assigments, allSensesVertices);
		double score = globalConnectivityMeasure(sensegraph);
		sensegraph.shutdown();
		return score;
	}

	@Override
	public abstract double globalConnectivityMeasure(Graph sensegraph);

	@Override
	public double globalConnectivityMeasure(Map<T, U> surfaceFormSenseAssigments, Graph subgraph,
			Map<T, List<U>> surfaceFormsSenses) {
		Collection<Vertex> assignments = ModelTransformer.verticesFromSenses(subgraph,
				surfaceFormSenseAssigments.values());
		Collection<Set<Vertex>> surfaceFormsVertices = ModelTransformer.wordsVerticesFromSenses(subgraph,
				surfaceFormsSenses);
		return globalConnectivityMeasure(assignments, subgraph, surfaceFormsVertices);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
