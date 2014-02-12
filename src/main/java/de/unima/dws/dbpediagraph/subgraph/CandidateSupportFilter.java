package de.unima.dws.dbpediagraph.subgraph;

import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Ordering;

import de.unima.dws.dbpediagraph.model.Sense;
import de.unima.dws.dbpediagraph.model.SurfaceForm;

/**
 * Static methods for filtering a list of {@link Sense}s per {@link SurfaceForm} based on their support values.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class CandidateSupportFilter {
	private static final Logger logger = LoggerFactory.getLogger(CandidateSupportFilter.class);

	/**
	 * Configuration key for filtering candidate senses by minimum support.
	 */
	private static final String CONFIG_CANDIDATE_MIN_SUPPORT = "org.dbpedia.spotlight.graphdb.filter.minSupport";
	/**
	 * Configuration key for filtering the best k candidate senses by support.
	 */
	private static final String CONFIG_CANDIDATE_BEST_K_SUPPORT = "org.dbpedia.spotlight.graphdb.filter.bestkSupport";

	private static Ordering<Sense> descSupport = new Ordering<Sense>() {
		@Override
		public int compare(Sense left, Sense right) {
			return Integer.compare(right.support(), left.support());
		}
	};

	/**
	 * Deletes all {@link DBpediaResource} from the map values which do not belong to the best k for each surface form.
	 * If k <= 0, the method does not do anything.
	 */
	public static <T extends SurfaceForm, U extends Sense> void filterBestkResourcesBySupport(
			Map<T, List<U>> sfsSenses, int k) {
		if (k <= 0)
			return;
		int filtered = 0;

		for (Entry<T, List<U>> entry : sfsSenses.entrySet()) {
			if (entry.getValue().size() > k) {
				List<U> unfilteredResources = entry.getValue();
				List<U> bestKResources = descSupport.greatestOf(unfilteredResources, k);
				sfsSenses.put(entry.getKey(), bestKResources);

				filtered += (unfilteredResources.size() - bestKResources.size());
			}
		}

		logger.info("Filtered " + filtered + " resource candidates by best " + k + " candidate filter.");
	}

	public static <T extends SurfaceForm, U extends Sense> void filterBestkResourcesByConfigSupport(
			Map<T, List<U>> sfsSenses, Configuration config) {
		int bestkSupport = config.getInt(CONFIG_CANDIDATE_BEST_K_SUPPORT, -1);
		filterBestkResourcesBySupport(sfsSenses, bestkSupport);
	}

	/**
	 * Removes all {@link DBpediaResource} from the map values with support < minSupport. If minSupport <= 0, the method
	 * returns right away.
	 */
	public static <T extends SurfaceForm, U extends Sense> void filterResourcesBySupport(Map<T, List<U>> sfsSenses,
			final int minSupport) {
		if (minSupport <= 0)
			return;
		int filtered = 0;

		for (Entry<T, List<U>> entry : sfsSenses.entrySet()) {
			List<U> unfilteredResources = entry.getValue();
			List<U> filteredResources = new ArrayList<>();
			for (U resource : unfilteredResources) {
				if (resource.support() >= minSupport)
					filteredResources.add(resource);
			}
			sfsSenses.put(entry.getKey(), filteredResources);

			filtered += (unfilteredResources.size() - filteredResources.size());
		}

		logger.info("Filtered " + filtered + " resource candidates with support < " + minSupport);
	}

	public static <T extends SurfaceForm, U extends Sense> void filterResourcesByConfigMinSupport(
			Map<T, List<U>> sfsSenses, Configuration config) {
		int minSupport = config.getInt(CONFIG_CANDIDATE_MIN_SUPPORT, -1);
		filterResourcesBySupport(sfsSenses, minSupport);
	}
}
