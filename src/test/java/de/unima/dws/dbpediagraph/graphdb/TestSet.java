package de.unima.dws.dbpediagraph.graphdb;

public class TestSet {
	public static class NavigliTestSet {
		/** Test Setup from Navigli&Lapata (2010) */
		public static final String NL_PKG = "/test-navigli";

		private static final String NL_SENSES = NL_PKG + "/nl-test.senses";
		private static final String NL_VERTICES = NL_PKG + "/nl-test.vertices";
		private static final String NL_EDGES = NL_PKG + "/nl-test.edges";

		private static final String NL_EXPECTED_VERTICES = NL_PKG + "/nl-expected-vertices";
		private static final String NL_EXPECTED_EDGES = NL_PKG + "/nl-expected-edges";

		/** Global Connectivity Measure Results for the example from Navigli&Lapata (2010) */
		public static final String NL_GLOBAL_RESULTS = NL_PKG + "/nl-global-test.results";
		/** Local Connectivity Measure Results for the example from Navigli&Lapata (2010) */
		public static final String NL_LOCAL_RESULTS = NL_PKG + "/nl-local-test.results";
	}

	private static final String CYCLIC_TEST_PKG = "/test-cyclic";

	String verticesFile;
	String edgesFile;
	String sensesFile;
	String expectedVerticesFile;

	String expectedEdgesFile;

	public static final TestSet NAVIGLI_FILE_NAMES = new TestSet(NavigliTestSet.NL_VERTICES, NavigliTestSet.NL_EDGES,
			NavigliTestSet.NL_SENSES, NavigliTestSet.NL_EXPECTED_VERTICES, NavigliTestSet.NL_EXPECTED_EDGES);

	public static final TestSet CYCLIC_FILE_NAMES = new TestSet(CYCLIC_TEST_PKG + "/vertices", CYCLIC_TEST_PKG
			+ "/edges", CYCLIC_TEST_PKG + "/senses", CYCLIC_TEST_PKG + "/subgraph-vertices", CYCLIC_TEST_PKG
			+ "/subgraph-edges");

	public TestSet(String verticesFile, String edgesFile, String sensesFile, String expectedVerticesFile,
			String expectedEdgesFile) {
		this.verticesFile = verticesFile;
		this.edgesFile = edgesFile;
		this.sensesFile = sensesFile;
		this.expectedVerticesFile = expectedVerticesFile;
		this.expectedEdgesFile = expectedEdgesFile;
	}
}
