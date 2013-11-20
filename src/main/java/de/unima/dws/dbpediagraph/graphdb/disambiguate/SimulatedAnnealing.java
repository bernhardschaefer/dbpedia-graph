package de.unima.dws.dbpediagraph.graphdb.disambiguate;

import java.util.*;
import java.util.Map.Entry;

import com.tinkerpop.blueprints.Graph;

import de.unima.dws.dbpediagraph.graphdb.model.Sense;
import de.unima.dws.dbpediagraph.graphdb.model.SurfaceForm;

public class SimulatedAnnealing<T extends SurfaceForm, U extends Sense> {

	private static final double MIN_IMPROVEMENT = 0.01;
	private static final double MIN_TEMPERATURE = 0.1;
	private final Map<T, List<U>> surfaceFormsSenses;
	private final Graph subgraph;
	private final ScoreFunction<T, U> scoreFunction;
	private final int maxU;
	private final double initialTemperature;
	private final Random random;

	public static interface ScoreFunction<T extends SurfaceForm, U extends Sense> {
		double getScore(Map<T, U> assignments, Graph subgraph);
	}

	public SimulatedAnnealing(Map<T, List<U>> surfaceFormsSenses, Graph subgraph, ScoreFunction<T, U> scoreFunction,
			int maxU, double initialTemperature, Random random) {
		this.surfaceFormsSenses = surfaceFormsSenses;
		this.subgraph = subgraph;
		this.scoreFunction = scoreFunction;
		this.maxU = maxU;
		this.initialTemperature = initialTemperature;
		this.random = random;
	}

	public SimulatedAnnealing(Map<T, List<U>> surfaceFormsSenses, Graph subgraph, ScoreFunction<T, U> scoreFunction,
			int maxU, double initialTemperature) {
		this(surfaceFormsSenses, subgraph, scoreFunction, maxU, initialTemperature, new Random());
	}

	public Map<T, U> search() {
		// Initially, we randomly select an interpretation I for sentence s.
		Map<T, U> assignments = randomSelect(surfaceFormsSenses);
		// get fitness
		double score = scoreFunction.getScore(assignments, subgraph);
		int u = 0;
		return doStep(assignments, score, u, initialTemperature);
	}

	// given: assignments, score(assignments), u, surfaceFormSenses
	private Map<T, U> doStep(Map<T, U> assignments, double score, int u, double temperature) {
		if (temperature < MIN_TEMPERATURE)
			return assignments;

		// The procedure is repeated u times. The algorithm terminates when we observe no changes in I after u steps.
		// Otherwise, the entire procedure is repeated starting from the most recent interpretation.
		if (u >= maxU) {
			// the constant T, initially set to 1.0, was reset to T := 0.9 * T after the u iterations.
			u = 0;
			temperature *= 0.9;
		}

		// At each step, we (randomly) select a word from s and assign it a new sense also chosen at random.
		// As a result, a new interpretation I' is produced.
		Map<T, U> newAssignments = swapRandomSense(assignments, surfaceFormsSenses);

		// Next, we apply our global measure to the graph induced by I'
		double newScore = scoreFunction.getScore(assignments, subgraph);

		// and calculate the difference delta between its value and that of the graph obtained from the old
		// interpretation I.
		double delta = (newScore - score);

		if (delta > MIN_IMPROVEMENT)
			// If the new interpretation has a higher score, we adopt it (i.e., we set I :=I').
			// assignments = newAssignments;
			doStep(newAssignments, newScore, 0, temperature);
		else {
			u++;
			// Otherwise, we either switch to the new interpretation with probability e^(delta/T),
			// or nonetheless retain the old interpretation with probability 1-e^(delta/T)
			double probability = Math.pow(Math.E, (delta / temperature));
			if (random.nextDouble() < probability)
				doStep(newAssignments, newScore, u, temperature);
			else
				doStep(assignments, score, u, temperature);
		}

		throw new IllegalStateException("this shouldnt happen");
	}

	private Map<T, U> swapRandomSense(Map<T, U> assignments, Map<T, List<U>> surfaceFormsSenses) {
		// get random surface form
		ArrayList<T> sfs = new ArrayList<>(assignments.keySet());
		T randomSurfaceForm = sfs.get(random.nextInt(sfs.size()));

		// get random sense
		List<U> otherSenses = new ArrayList<>(surfaceFormsSenses.get(randomSurfaceForm));
		otherSenses.remove(randomSurfaceForm);
		U newSense = otherSenses.get(random.nextInt(otherSenses.size()));

		// override old sense of surface form with new sense
		assignments.put(randomSurfaceForm, newSense);

		return assignments;
	}

	private Map<T, U> randomSelect(Map<T, List<U>> surfaceFormsSenses) {
		Map<T, U> randomSelection = new HashMap<>();
		for (Entry<T, List<U>> entry : surfaceFormsSenses.entrySet()) {
			List<U> list = entry.getValue();
			U element = list.get(random.nextInt(list.size()));
			randomSelection.put(entry.getKey(), element);
		}
		return randomSelection;
	}
}
