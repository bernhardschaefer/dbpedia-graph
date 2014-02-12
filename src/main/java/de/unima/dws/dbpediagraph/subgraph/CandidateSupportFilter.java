package de.unima.dws.dbpediagraph.subgraph;

import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.*;

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

	private static Ordering<Sense> ascSupport = new Ordering<Sense>() {
		@Override
		public int compare(Sense left, Sense right) {
			return Integer.compare(left.support(), right.support());
		}
	};

	/**
	 * Returns a map without the map values which do not belong to the best k for each surface form. If k <= 0, the
	 * method returns the unmodified map, otherwise a new map instance is returned.
	 */
	public static <T extends SurfaceForm, U extends Sense> Map<T, List<U>> filterBestkSensesBySupport(
			Map<T, List<U>> sfsSenses, int k) {
		if (k <= 0)
			return sfsSenses;
		Map<T, List<U>> filteredSfssSenses = new HashMap<T, List<U>>();
		int filteredCounter = 0;
		for (Entry<T, List<U>> entry : sfsSenses.entrySet()) {
			if (entry.getValue().size() > k) {
				List<U> unfilteredSenses = entry.getValue();
				List<U> bestKSenses = ascSupport.greatestOf(unfilteredSenses, k);
				filteredSfssSenses.put(entry.getKey(), bestKSenses);
				filteredCounter += (unfilteredSenses.size() - bestKSenses.size());
			} else
				filteredSfssSenses.put(entry.getKey(), entry.getValue());
		}
		logger.info("Filtered " + filteredCounter + " sense candidates by best " + k + " candidate filter.");
		return filteredSfssSenses;
	}

	public static <T extends SurfaceForm, U extends Sense> Map<T, List<U>> filterBestkSensesByConfigSupport(
			Map<T, List<U>> sfsSenses, Configuration config) {
		int bestkSupport = config.getInt(CONFIG_CANDIDATE_BEST_K_SUPPORT, -1);
		return filterBestkSensesBySupport(sfsSenses, bestkSupport);
	}

	/**
	 * Returns a map without all {@link Sense}s from the map values with support < minSupport. If minSupport <= 0, the
	 * method returns the unmodified map, otherwise a new map instance is returned.
	 */
	public static <T extends SurfaceForm, U extends Sense> Map<T, List<U>> filterSensesBySupport(
			Map<T, List<U>> sfsSenses, final int minSupport) {
		if (minSupport <= 0)
			return sfsSenses;
		Predicate<U> minSupPred = new Predicate<U>() {
			@Override
			public boolean apply(U sense) {
				return sense.support() >= minSupport;
			}
		};
		Map<T, List<U>> filteredSfssSenses = new HashMap<T, List<U>>();
		int filteredCounter = 0;
		for (Entry<T, List<U>> entry : sfsSenses.entrySet()) {
			List<U> unfilteredSenses = entry.getValue();
			List<U> filteredSenses = Lists.newArrayList(Iterables.filter(unfilteredSenses, minSupPred));
			filteredSfssSenses.put(entry.getKey(), filteredSenses);
			filteredCounter += (unfilteredSenses.size() - filteredSenses.size());
		}
		logger.info("Filtered " + filteredCounter + " sense candidates with support < " + minSupport);
		return filteredSfssSenses;
	}

	public static <T extends SurfaceForm, U extends Sense> Map<T, List<U>> filterSensesByConfigMinSupport(
			Map<T, List<U>> sfsSenses, Configuration config) {
		int minSupport = config.getInt(CONFIG_CANDIDATE_MIN_SUPPORT, -1);
		return filterSensesBySupport(sfsSenses, minSupport);
	}
}
