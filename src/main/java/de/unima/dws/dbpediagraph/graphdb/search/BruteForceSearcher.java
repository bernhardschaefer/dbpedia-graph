package de.unima.dws.dbpediagraph.graphdb.search;

import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.lang.mutable.MutableInt;

import com.tinkerpop.blueprints.Graph;

import de.unima.dws.dbpediagraph.graphdb.model.Sense;
import de.unima.dws.dbpediagraph.graphdb.model.SurfaceForm;

//TODO what happens if a surface form has no senses
class BruteForceSearcher implements Searcher {

	@Override
	public <T extends SurfaceForm, U extends Sense> Map<T, U> search(Map<T, List<U>> surfaceFormsSenses,
			Graph subgraph, ConnectivityMeasureFunction<T, U> measureFunction) {
		Map<T, U> assignment = getStartAssignment(surfaceFormsSenses);
		double maxScore = measureFunction.getMeasure(assignment, subgraph);
		Map<T, U> maxAssignment = assignment;

		Map<Integer, T> surfaceFormMappings = createSurfaceFormMappings(surfaceFormsSenses.keySet());
		MutableInt listPointer = new MutableInt(0);
		while (true) {
			assignment = getNextAssignment(assignment, surfaceFormsSenses, listPointer, surfaceFormMappings);
			if (assignment == null)
				break; // we're done

			double score = measureFunction.getMeasure(assignment, subgraph);
			if (score > maxScore) {
				maxAssignment = assignment;
				maxScore = score;
			}
		}
		return maxAssignment;
	}

	public static <T extends SurfaceForm, U extends Sense> Map<Integer, T> createSurfaceFormMappings(Set<T> surfaceForms) {
		Map<Integer, T> surfaceFormMappings = new HashMap<>(surfaceForms.size());
		int i = 0;
		Iterator<T> iter = surfaceForms.iterator();
		while (iter.hasNext()) {
			T surfaceForm = iter.next();
			surfaceFormMappings.put(i++, surfaceForm);
		}
		return surfaceFormMappings;
	}

	public static <T extends SurfaceForm, U extends Sense> Map<T, U> getNextAssignment(Map<T, U> currentAssignment,
			Map<T, List<U>> surfaceFormsSenses, MutableInt listPointer, Map<Integer, T> surfaceFormMappings) {
		Map<T, U> nextAssignment = new HashMap<>(currentAssignment);
		T surfaceForm = surfaceFormMappings.get(listPointer.intValue());
		List<U> senses = surfaceFormsSenses.get(surfaceForm);
		U currentSense = currentAssignment.get(surfaceForm);
		int indexCurrentSense = senses.indexOf(currentSense);
		if (indexCurrentSense < (senses.size() - 1)) {
			// we can stay at the same surface form and just use the next sense
			U nextSense = senses.get(indexCurrentSense + 1);
			nextAssignment.put(surfaceForm, nextSense);
		} else {
			// we have covered all senses of the surface forms
			// now we have to move on to the next surface form
			listPointer.add(1);
			T newSurfaceForm = surfaceFormMappings.get(listPointer.intValue());
			if (newSurfaceForm == null)
				// there are no next assignments
				return null;
			// do a recursive call to find the next sense in the next surface form
			return getNextAssignment(currentAssignment, surfaceFormsSenses, listPointer, surfaceFormMappings);
		}
		return nextAssignment;
	}

	private static <T extends SurfaceForm, U extends Sense> Map<T, U> getStartAssignment(
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
