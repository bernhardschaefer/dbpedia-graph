package de.unima.dws.dbpediagraph.graphdb;

import de.unima.dws.dbpediagraph.graphdb.disambiguate.GraphDisambiguator;

/**
 * Tests a {@link GraphDisambiguator} class using a {@link SubgraphTester} and a {@link TestSet}. Implementations are to
 * be used in JUnit tests.
 * 
 * @author Bernhard Schäfer
 * 
 */
public interface DisambiguationTester {

	/**
	 * Compare the expected and actual disambiguation results.
	 * 
	 * @throws AssertionError
	 *             if the expected and actual results differ
	 */
	public void compareAllDisambiguationResults();

	ExpectedDisambiguationResults getExpectedDisambiguationResults();

//	List<SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense>> getActualDisambiguationResults();

}
