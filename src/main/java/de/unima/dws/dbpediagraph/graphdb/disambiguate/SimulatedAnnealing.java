package de.unima.dws.dbpediagraph.graphdb.disambiguate;

import java.util.*;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Graph;

import de.unima.dws.dbpediagraph.graphdb.model.Sense;
import de.unima.dws.dbpediagraph.graphdb.model.SurfaceForm;

/**
 * @author Bernhard Schäfer
 */
public class SimulatedAnnealing<T extends SurfaceForm, U extends Sense> implements Searcher<T, U> {
	private static final Logger logger = LoggerFactory.getLogger(SimulatedAnnealing.class);

	private static final double MIN_IMPROVEMENT = 0.01;
	private static final double MIN_TEMPERATURE = 0.1;
	private Map<T, List<U>> surfaceFormsSenses;
	private Graph subgraph;
	private ScoreFunction<T, U> scoreFunction;
	private final int maxU;
	private final double initialTemperature;
	private final Random random;

	private double currentScore;

	public static interface ScoreFunction<T extends SurfaceForm, U extends Sense> {
		double getScore(Map<T, U> assignments, Graph subgraph);
	}

	public SimulatedAnnealing(int maxU, double initialTemperature, Random random) {
		this.maxU = maxU;
		this.initialTemperature = initialTemperature;
		this.random = random;
	}

	@Override
	public Map<T, U> search(Map<T, List<U>> surfaceFormsSenses, Graph subgraph, ScoreFunction<T, U> scoreFunction) {
		this.surfaceFormsSenses = surfaceFormsSenses;
		this.subgraph = subgraph;
		this.scoreFunction = scoreFunction;

		logger.info("Starting " + toString());
		// Initially, we randomly select an interpretation I for sentence s.
		Map<T, U> assignments = randomSelect(surfaceFormsSenses);
		// get fitness
		double score = scoreFunction.getScore(assignments, subgraph);
		currentScore = score;

		int u = 0;
		int totalSteps = 0;

		Map<T, U> bestAssignment = doStep(assignments, score, u, initialTemperature, totalSteps);
		logger.info("Finished " + getClass().getSimpleName() + ". Best assignment: " + bestAssignment);
		return bestAssignment;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " maxU:" + maxU + " initialTemeperature: " + initialTemperature + " sFS: "
				+ surfaceFormsSenses;
	}

	// given: assignments, score(assignments), u, surfaceFormSenses
	private Map<T, U> doStep(Map<T, U> assignments, double score, int u, double temperature, int totalSteps) {
		currentScore = score;

		if (temperature < MIN_TEMPERATURE) {
			logger.info("{} total steps in simulated annealing", totalSteps);
			return assignments;
		}

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
			doStep(newAssignments, newScore, 0, temperature, totalSteps++);
		else {
			u++;
			// Otherwise, we either switch to the new interpretation with probability e^(delta/T),
			// or nonetheless retain the old interpretation with probability 1-e^(delta/T)
			double probability = Math.pow(Math.E, (delta / temperature));
			if (random.nextDouble() < probability)
				doStep(newAssignments, newScore, u, temperature, totalSteps++);
			else
				doStep(assignments, score, u, temperature, totalSteps++);
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

	// TODO fix ugly hack and think about datastructure that contains assignments and score
	@Override
	public double getScore() {
		return currentScore;
	}

}
