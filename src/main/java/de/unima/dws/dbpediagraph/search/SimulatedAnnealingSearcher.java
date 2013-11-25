package de.unima.dws.dbpediagraph.search;

import java.util.*;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Graph;

import de.unima.dws.dbpediagraph.model.Sense;
import de.unima.dws.dbpediagraph.model.SurfaceForm;

/**
 * Simulated Annealing search. Code adapted from Artificial Intelligence: A Modern Approach
 * 
 * @author Bernhard Schäfer
 */
class SimulatedAnnealingSearcher implements Searcher {
	private static final Logger logger = LoggerFactory.getLogger(SimulatedAnnealingSearcher.class);

	private final Scheduler scheduler;
	private static final Random RANDOM = new Random();

	SimulatedAnnealingSearcher(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	@Override
	public <T extends SurfaceForm, U extends Sense> Map<T, U> search(Map<T, List<U>> surfaceFormsSenses,
			Graph subgraph, ConnectivityMeasureFunction<T, U> measureFunction) {
		logger.info("Starting " + toString());

		Map<T, U> bestAssignment = searchIterative(surfaceFormsSenses, subgraph, measureFunction);

		logger.info("Finished " + getClass().getSimpleName() + ". Best assignment: " + bestAssignment);
		return bestAssignment;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " scheduler: " + scheduler.toString();
	}

	private <T extends SurfaceForm, U extends Sense> Map<T, U> searchIterative(Map<T, List<U>> surfaceFormsSenses,
			Graph subgraph, ConnectivityMeasureFunction<T, U> measureFunction) {
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
				if (RANDOM.nextDouble() < probability) {
					assignments = newAssignments;
					score = newScore;
				}
			}
		}
	}

	private static <T extends SurfaceForm, U extends Sense> Map<T, U> newSwapRandomSense(Map<T, U> assignments,
			Map<T, List<U>> surfaceFormsSenses) {
		return swapRandomSense(new HashMap<>(assignments), surfaceFormsSenses);
	}

	private static <T extends SurfaceForm, U extends Sense> Map<T, U> swapRandomSense(Map<T, U> assignments,
			Map<T, List<U>> surfaceFormsSenses) {
		// get random surface form
		ArrayList<T> sfs = new ArrayList<>(assignments.keySet());
		T randomSurfaceForm = sfs.get(RANDOM.nextInt(sfs.size()));

		// get random sense
		List<U> otherSenses = new ArrayList<>(surfaceFormsSenses.get(randomSurfaceForm));
		otherSenses.remove(randomSurfaceForm);
		U newSense = otherSenses.get(RANDOM.nextInt(otherSenses.size()));

		// override old sense of surface form with new sense
		assignments.put(randomSurfaceForm, newSense);

		return assignments;
	}

	private static <T extends SurfaceForm, U extends Sense> Map<T, U> randomSelect(Map<T, List<U>> surfaceFormsSenses) {
		Map<T, U> randomSelection = new HashMap<>();
		for (Entry<T, List<U>> entry : surfaceFormsSenses.entrySet()) {
			List<U> list = entry.getValue();
			U element = list.get(RANDOM.nextInt(list.size()));
			randomSelection.put(entry.getKey(), element);
		}
		return randomSelection;
	}

}
