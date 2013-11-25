package de.unima.dws.dbpediagraph.search;

import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.lang.mutable.MutableInt;

import com.tinkerpop.blueprints.Graph;

import de.unima.dws.dbpediagraph.model.Sense;
import de.unima.dws.dbpediagraph.model.SurfaceForm;

/**
 * Brute-force searcher that finds the highest score for all possible permutations of surface form <-> sense
 * assignments.
 * 
 * @author Bernhard Schäfer
 * 
 */
// TODO what happens if a surface form has no senses
class BruteForceSearcher implements Searcher {

	@Override
	public <T extends SurfaceForm, U extends Sense> Map<T, U> search(Map<T, List<U>> surfaceFormsSenses,
			Graph subgraph, ConnectivityMeasureFunction<T, U> measureFunction) {
		Map<T, U> assignment = createInitialAssignment(surfaceFormsSenses);
		double maxScore = measureFunction.getMeasure(assignment, subgraph);
		Map<T, U> maxAssignment = new HashMap<>(assignment);

		Map<Integer, T> surfaceFormMappings = createSurfaceFormMappings(surfaceFormsSenses.keySet());
		MutableInt surfaceFormPointer = new MutableInt(0); // tracks which surface form's sense is increased each
															// iteration
		while ((assignment = getNextAssignment(assignment, surfaceFormsSenses, surfaceFormPointer, surfaceFormMappings)) != null) {
			double score = measureFunction.getMeasure(assignment, subgraph);
			if (score > maxScore) {
				maxAssignment = new HashMap<>(assignment);
				maxScore = score;
			}
		}
		return maxAssignment;
	}

	/**
	 * For iteration an explicit Integer->SurfaceFormKey is needed. Since map key ordering is arbitrary, this mapping is
	 * preserved in another map.
	 */
	private static <T extends SurfaceForm, U extends Sense> Map<Integer, T> createSurfaceFormMappings(
			Set<T> surfaceForms) {
		Map<Integer, T> surfaceFormMappings = new HashMap<>(surfaceForms.size());
		int i = 0;
		Iterator<T> iter = surfaceForms.iterator();
		while (iter.hasNext()) {
			T surfaceForm = iter.next();
			surfaceFormMappings.put(i++, surfaceForm);
		}
		return surfaceFormMappings;
	}

	private static <T extends SurfaceForm, U extends Sense> Map<T, U> getNextAssignment(Map<T, U> currentAssignment,
			Map<T, List<U>> surfaceFormsSenses, MutableInt listPointer, Map<Integer, T> surfaceFormMappings) {
		T surfaceForm = surfaceFormMappings.get(listPointer.intValue());
		List<U> senses = surfaceFormsSenses.get(surfaceForm);
		int indexCurrentSense = senses.indexOf(currentAssignment.get(surfaceForm));
		if (indexCurrentSense < (senses.size() - 1)) {
			// we can stay at the same surface form and just use the next sense
			U nextSense = senses.get(indexCurrentSense + 1);
			currentAssignment.put(surfaceForm, nextSense);
		} else
			// we have to increase the next surface form's sense
			currentAssignment = checkNextSurfaceForm(currentAssignment, surfaceFormsSenses, listPointer,
					surfaceFormMappings);
		return currentAssignment;
	}

	// we have covered all senses of the surface forms
	private static <T extends SurfaceForm, U extends Sense> Map<T, U> checkNextSurfaceForm(Map<T, U> currentAssignment,
			Map<T, List<U>> surfaceFormsSenses, MutableInt listPointer, Map<Integer, T> surfaceFormMappings) {
		// reset the senses of the surface form and all smaller surface forms to zero
		for (int i = 0; i <= listPointer.intValue(); i++) {
			T surfaceForm = surfaceFormMappings.get(i);
			List<U> senses = surfaceFormsSenses.get(surfaceForm);
			currentAssignment.put(surfaceForm, senses.get(0));
		}

		// move to the next the surface form
		listPointer.add(1);
		T surfaceForm = surfaceFormMappings.get(listPointer.intValue());
		if (surfaceForm == null)
			// we have reached the highest surface form already; we are done
			return null;
		List<U> senses = surfaceFormsSenses.get(surfaceForm);
		int indexCurrentSense = senses.indexOf(currentAssignment.get(surfaceForm));
		if (indexCurrentSense < (senses.size() - 1)) {
			U nextSense = senses.get(indexCurrentSense + 1);
			currentAssignment.put(surfaceForm, nextSense);
		} else
			return checkNextSurfaceForm(currentAssignment, surfaceFormsSenses, listPointer, surfaceFormMappings);

		// reset the pointer to the smallest surface form
		listPointer.setValue(0);

		return currentAssignment;
	}

	/**
	 * Creates the initial surface form sense assignment by using the first sense in the list of sense candidates for
	 * each surface form.
	 * 
	 * @param surfaceFormsSenses
	 *            the mapping of surface forms to sense candidates
	 */
	private static <T extends SurfaceForm, U extends Sense> Map<T, U> createInitialAssignment(
			Map<T, List<U>> surfaceFormsSenses) {
		Map<T, U> assignment = new HashMap<>();
		for (Entry<T, List<U>> entry : surfaceFormsSenses.entrySet()) {
			List<U> list = entry.getValue();
			U element = list.get(0);
			assignment.put(entry.getKey(), element);
		}
		return assignment;
	}

}
