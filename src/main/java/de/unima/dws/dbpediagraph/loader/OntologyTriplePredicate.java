package de.unima.dws.dbpediagraph.loader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

import org.apache.commons.configuration.Configuration;

import com.google.common.base.Predicate;

import de.unima.dws.dbpediagraph.util.FileUtils;

/**
 * Filters RDF Triples if the object is an ontology class and occurs more frequently than the provided threshold. This
 * is necessary to filter common classes like owl#Thing or Person.
 * 
 * @author Bernhard Schäfer
 * 
 */
class OntologyTriplePredicate implements Predicate<Triple> {
	private static final String CONFIG_OCCURRENCE_COUNTS_FILE = "loading.filter.ontology.occurrences.file";
	private static final String CONFIG_ONTOLOGY_THRESHOLD = "loading.filter.ontology.threshold";

	private final Map<String, Integer> uriCounts;
	private final int threshold;

	OntologyTriplePredicate(Map<String, Integer> uriCounts, int threshold) {
		this.uriCounts = uriCounts;
		this.threshold = threshold;
	}

	static OntologyTriplePredicate fromConfig(Configuration config) {
		Map<String, Integer> uriCounts = new HashMap<>();

		String fileName = config.getString(CONFIG_OCCURRENCE_COUNTS_FILE);

		List<String> lines;
		try {
			lines = FileUtils.readNonEmptyNonCommentLinesFromFile(OntologyTriplePredicate.class, fileName);
		} catch (IOException | URISyntaxException e) {
			throw new RuntimeException("Filter " + fileName + " could not be loaded.", e);
		}
		for (String line : lines) {
			String[] splits = line.split(" ");
			int count = Integer.parseInt(splits[0]);
			String uri = splits[1];
			uriCounts.put(uri, count);
		}

		int threshold = config.getInt(CONFIG_ONTOLOGY_THRESHOLD);

		return new OntologyTriplePredicate(uriCounts, threshold);
	}

	private boolean isValid(String uri) {
		if (!uri.startsWith(TriplePredicate.ONTOLOGY_PREFIX))
			return true;
		int count = uriCounts.get(uri);
		return count <= threshold;
	}

	@Override
	public boolean apply(Triple t) {
		return isValid(t.object());
	}

}
