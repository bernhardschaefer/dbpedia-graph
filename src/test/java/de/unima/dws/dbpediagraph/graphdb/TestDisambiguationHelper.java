package de.unima.dws.dbpediagraph.graphdb;

import static org.junit.Assert.assertEquals;

import java.util.*;
import java.util.Map.Entry;

import de.unima.dws.dbpediagraph.graphdb.model.*;

public class TestDisambiguationHelper {

	public static final String SENSES_DELIMITER = ",";

	public static void compareDisambiguatedAssignment(ExpectedDisambiguationResults allExpected, List<SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense>> actual, Class<?> disambiguatorClass) {
		List<SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense>> expected = getHighestGlobalScoreResult(allExpected, disambiguatorClass);
		assertEquals(expected.size(), actual.size());
		for(int i = 0; i < expected.size(); i++)
			assertEquals(expected.get(i), actual.get(i));
	}

	public static List<SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense>> getHighestGlobalScoreResult(
			ExpectedDisambiguationResults allExpected, Class<?> disambiguatorClass) {
		List<SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense>> highest = null;
		for (Entry<String, Map<Class<?>, Double>> entry : allExpected.getResults().entrySet()) {
			double expected = entry.getValue().get(disambiguatorClass);
			if(highest == null || expected >= highest.get(0).score()) {
				highest = transform(entry.getKey(),expected);
			}
		}
		return highest;
	}

	public static List<SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense>> transform(String key, double score) {
		List<SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense>> res = new ArrayList<>();
		Collection<String> senseAssignments = split(key);
		for(String sense: senseAssignments) {
			SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense> sfss = DefaultModelFactory.INSTANCE.newSurfaceFormSenseScore(new DefaultSurfaceForm(sense.substring(sense.length()-2, sense.length())), new DefaultSense(sense), score);
			res.add(sfss);
		}
		return res;
	}
	
	public static Collection<String> split(String key) {
		return Arrays.asList(key.split(SENSES_DELIMITER));
	}


}
