package de.unima.dws.dbpediagraph.graphdb;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.util.CollectionUtils;
import de.unima.dws.dbpediagraph.graphdb.util.FileUtils;

public class SubgraphTestData {

	public static class FileNames {
		String verticesFile;
		String edgesFile;
		String sensesFile;
		String expectedVerticesFile;
		String expectedEdgesFile;

		public static final FileNames NAVIGLI_FILE_NAMES = new FileNames(NL_VERTICES, NL_EDGES, NL_SENSES,
				NL_EXPECTED_VERTICES, NL_EXPECTED_EDGES);

		public FileNames(String verticesFile, String edgesFile, String sensesFile, String expectedVerticesFile,
				String expectedEdgesFile) {
			this.verticesFile = verticesFile;
			this.edgesFile = edgesFile;
			this.sensesFile = sensesFile;
			this.expectedVerticesFile = expectedVerticesFile;
			this.expectedEdgesFile = expectedEdgesFile;
		}
	}

	/** Test Setup from Navigli&Lapata (2010) */
	public static final String NL_PKG = "/test-navigli";

	private static final String NL_SENSES = NL_PKG + "/nl-test.senses";
	private static final String NL_VERTICES = NL_PKG + "/nl-test.vertices";
	private static final String NL_EDGES = NL_PKG + "/nl-test.edges";

	private static final String NL_EXPECTED_VERTICES = NL_PKG + "/nl-expected-vertices";
	private static final String NL_EXPECTED_EDGES = NL_PKG + "/nl-expected-edges";

	public static final SubgraphTestData newNavigliTestData() {
		return new SubgraphTestData(FileNames.NAVIGLI_FILE_NAMES);
	}

	public final Graph graph;
	public final Collection<Collection<Vertex>> allWordsSenses;
	public final Collection<Vertex> allSenses;

	public final List<String> expectedEdges;
	public final List<String> expectedVertices;

	public SubgraphTestData(FileNames fileNames) {
		this(fileNames.verticesFile, fileNames.edgesFile, fileNames.sensesFile, fileNames.expectedVerticesFile,
				fileNames.expectedEdgesFile);
	}

	public SubgraphTestData(String verticesFile, String edgesFile, String sensesFile, String expectedVerticesFile,
			String expectedEdgesFile) {
		try {
			graph = FileUtils.parseGraph(verticesFile, edgesFile, getClass());
			allWordsSenses = FileUtils.parseAllWordsSenses(graph, sensesFile, getClass(), "");
			allSenses = CollectionUtils.combine(allWordsSenses);

			expectedVertices = FileUtils.readRelevantLinesFromFile(getClass(), expectedVerticesFile);
			expectedEdges = FileUtils.readRelevantLinesFromFile(getClass(), expectedEdgesFile);

		} catch (IOException | URISyntaxException e) {
			throw new RuntimeException("Error while trying to construct test graph.", e);
		}
	}

	public void close() {
		if (graph != null)
			graph.shutdown();
	}

}
