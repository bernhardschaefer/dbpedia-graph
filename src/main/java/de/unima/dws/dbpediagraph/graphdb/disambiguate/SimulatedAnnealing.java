package de.unima.dws.dbpediagraph.graphdb.disambiguate;

import java.util.*;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Graph;

import de.unima.dws.dbpediagraph.graphdb.model.Sense;
import de.unima.dws.dbpediagraph.graphdb.model.SurfaceForm;

/**
 * Simulated Annealing search. Code adapted from Artificial Intelligence: A Modern Approach
 * 
 * @author Bernhard Schäfer
 */
public class SimulatedAnnealing<T extends SurfaceForm, U extends Sense> implements Searcher<T, U> {
	private static final Logger logger = LoggerFactory.getLogger(SimulatedAnnealing.class);

	private final Scheduler scheduler;
	private ConnectivityMeasureFunction<T, U> measureFunction;
	private final Random random;

	public SimulatedAnnealing(Scheduler scheduler, ConnectivityMeasureFunction<T, U> measureFunction, Random random) {
		this.scheduler = scheduler;
		this.measureFunction = measureFunction;
		this.random = random;
	}

	private static final Random DEFAULT_RANDOM = new Random();

	public SimulatedAnnealing(Scheduler scheduler, ConnectivityMeasureFunction<T, U> measureFunction) {
		this(scheduler, measureFunction, DEFAULT_RANDOM);
	}

	@Override
	public Map<T, U> search(Map<T, List<U>> surfaceFormsSenses, Graph subgraph) {
		logger.info("Starting " + toString());

		Map<T, U> bestAssignment = searchIterative(surfaceFormsSenses, subgraph);

		logger.info("Finished " + getClass().getSimpleName() + ". Best assignment: " + bestAssignment);
		return bestAssignment;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " scheduler: " + scheduler.toString();
	}

	private Map<T, U> searchIterative(Map<T, List<U>> surfaceFormsSenses, Graph subgraph) {
		Map<T, U> assignments = randomSelect(surfaceFormsSenses);
		double score = measureFunction.getMeasure(assignments, subgraph);

		for (int i = 0;; i++) {
			double temperature = scheduler.getTemperature(i);
			if (temperature <= 0) {
				// we're done
				logger.info("{} total iterations in simulated annealing", i);
				return assignments;
			}

			// swap one random sense of a random surface form
			Map<T, U> newAssignments = newSwapRandomSense(assignments, surfaceFormsSenses);
			double newScore = measureFunction.getMeasure(newAssignments, subgraph);

			double delta = (newScore - score);
			if (delta > 0) {
				// If new assignment has higher score, we adopt it
				assignments = newAssignments;
				score = newScore;
			} else {
				// Otherwise, we only switch to new assignment with probability e^(delta/T),
				double probability = Math.pow(Math.E, (delta / temperature));
				if (random.nextDouble() < probability) {
					assignments = newAssignments;
					score = newScore;
				}
			}
		}
	}

	private Map<T, U> newSwapRandomSense(Map<T, U> assignments, Map<T, List<U>> surfaceFormsSenses) {
		return swapRandomSense(new HashMap<>(assignments), surfaceFormsSenses);
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
