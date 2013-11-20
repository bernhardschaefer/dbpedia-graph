package de.unima.dws.dbpediagraph.graphdb.disambiguate;

import java.util.*;
import java.util.Map.Entry;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.disambiguate.SimulatedAnnealing.ScoreFunction;
import de.unima.dws.dbpediagraph.graphdb.model.*;
import de.unima.dws.dbpediagraph.graphdb.subgraph.*;
import de.unima.dws.dbpediagraph.graphdb.util.CollectionUtils;

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

		// IDEA: like Simulated Annealing but keep best k in a bounded datastructure

		throw new UnsupportedOperationException("bestK not supported for global disambiguators");
	}

	@Override
	public List<SurfaceFormSenseScore<T, U>> disambiguate(Map<T, List<U>> surfaceFormsSenses, Graph subgraph) {
		// TODO check
		// http://docs.jboss.org/drools/release/latest/optaplanner-docs/html_single/index.html#optimizationAlgorithms
		// TODO check ai book code: https://code.google.com/p/aima-java/

		ScoreFunction<T, U> scoreFunction = new ScoreFunction<T, U>() {
			@Override
			public double getScore(Map<T, U> assignments, Graph subgraph) {
				return globalConnectivityMeasure(assignments, subgraph);
			}
		};

		// The number of iterations u for SA was set to 5,000
		int maxU = 5000;

		// and, following [56], the constant T, initially set to 1.0, was reset to T := 0.9 * T after the u iterations.
		double initialTemperature = 1.0;
		SimulatedAnnealing<T, U> sa = new SimulatedAnnealing<T, U>(surfaceFormsSenses, subgraph, scoreFunction, maxU,
				initialTemperature);

		Map<T, U> result = sa.search();
		return wrap(result);

		// private static class ConnectivityHeuristicFunction implements HeuristicFunction {
		// @Override
		// public double h(Object state) {
		// // TODO Auto-generated method stub
		// return 0;
		// }
		// };
		//
		// Object initialState = null;
		//
		// ActionsFunction actionsFunction = new ActionsFunction() {
		// @Override
		// public Set<Action> actions(Object s) {
		// // TODO Auto-generated method stub
		// return null;
		// }
		// };
		// ResultFunction resultFunction = new ResultFunction() {
		// @Override
		// public Object result(Object s, Action a) {
		// // TODO Auto-generated method stub
		// return null;
		// }
		// };
		// GoalTest goalTest = new GoalTest() {
		// @Override
		// public boolean isGoalState(Object state) {
		// // TODO Auto-generated method stub
		// return false;
		// }
		// };
		// Problem p = new Problem(initialState, actionsFunction, resultFunction, goalTest);
		// HeuristicFunction hf = new ConnectivityHeuristicFunction();
		// SimulatedAnnealingSearch sas = new SimulatedAnnealingSearch(hf);
		// try {
		// sas.search(p);
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}

	private List<SurfaceFormSenseScore<T, U>> wrap(Map<T, U> result) {
		List<SurfaceFormSenseScore<T, U>> scoresResults = new ArrayList<>();
		for (Entry<T, U> entry : result.entrySet()) {
			scoresResults.add(factory.newSurfaceFormSenseScore(entry.getKey(), entry.getValue(), 1));
		}
		return scoresResults;
	}

	@Override
	public double globalConnectivityMeasure(Collection<Vertex> surfaceFormSenseAssigments, Graph subgraph) {
		SubgraphConstruction sensegraphConstruction = SubgraphConstructionFactory.newSubgraphConstruction(subgraph,
				subgraphConstructionSettings);
		Graph sensegraph = sensegraphConstruction.createSubgraph(CollectionUtils.split(surfaceFormSenseAssigments));
		double score = globalConnectivityMeasure(sensegraph);
		sensegraph.shutdown();
		return score;
	}

	@Override
	public abstract double globalConnectivityMeasure(Graph sensegraph);

	@Override
	public double globalConnectivityMeasure(Map<T, U> surfaceFormSenseAssigments, Graph subgraph) {
		Collection<Vertex> vertices = ModelTransformer
				.verticesFromSenses(subgraph, surfaceFormSenseAssigments.values());
		return globalConnectivityMeasure(vertices, subgraph);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
