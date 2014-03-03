package de.unima.dws.dbpediagraph.loader;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.configuration.Configuration;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import de.unima.dws.dbpediagraph.graph.GraphConfig;
import de.unima.dws.dbpediagraph.util.EnumUtils;

/**
 * Triple predicates for {@link Predicate}s that can be used for filtering {@link Triple}s while loading the graph from
 * triple dumps.
 * 
 * @author Bernhard Schäfer
 * 
 */
enum TriplePredicate implements Predicate<Triple> {
	ALL {
		@Override
		public boolean apply(Triple t) {
			return true;
		}
	},
	BLACKLIST {
		private final Predicate<Triple> pred = BlacklistTriplePredicate.fromConfig(GraphConfig.config());

		@Override
		public boolean apply(Triple t) {
			return pred.apply(t);
		}
	},
	/**
	 * Filter that yields all triple with objects of DBpedia resources, ontologies, or categories.
	 */
	COMPLETE {
		/**
		 * valid uri prefixes from http://dbpedia.org/snorql/.
		 */
		// TODO(if needed) check if compiled pattern with all prefixes has better performance
		private final String[] uriPrefixes = new String[] { "http://dbpedia.org/", // dbpedia resource, property
				"http://www.w3.org/", // owl,xsd,rdfs,rdf,skos
				"http://xmlns.com/foaf", // foaf
				"http://purl.org/dc", // dc
		};

		@Override
		public boolean apply(Triple t) {
			for (String uriPrefix : uriPrefixes)
				if (t.object().startsWith(uriPrefix))
					return true;
			return false;
		}
	},
	/**
	 * Filter that yields all triple with DBpedia resources as subject and object, which means the match the pattern
	 * http://dbpedia.org/resource/*. This excludes the entire DBpedia ontology.
	 */
	RESOURCE {
		@Override
		public boolean apply(Triple t) {
			return t.object().startsWith("http://dbpedia.org/resource/");
		}
	},
	/**
	 * Yield all triples where objects and subjects are not a DBpedia category.
	 */
	NON_CATEGORY {
		@Override
		public boolean apply(Triple t) {
			// Checking for subjects is necessary since there are also triples with category subjects
			// see e.g. topical_concepts_en.nt dump file
			return !t.object().startsWith(CATEGORY_PREFIX) && !t.subject().startsWith(CATEGORY_PREFIX);
		}

	},
	/**
	 * Yield all triples that do not have a subject or object category matching one of the patterns.
	 */
	REGEXP_CATEGORY {
		private final List<Pattern> patterns = Arrays
				.asList(Pattern.compile("^\\d+.*$", Pattern.CASE_INSENSITIVE),
						Pattern.compile("^.*\\d+$", Pattern.CASE_INSENSITIVE),
						Pattern.compile("^list.*", Pattern.CASE_INSENSITIVE),
						Pattern.compile(
								"^.*(wiki|disambiguation|templates|portal|categories|articles|pages|redirect|navigational_boxes|infoboxes|stub).*",
								Pattern.CASE_INSENSITIVE) //
				);

		@Override
		public boolean apply(Triple t) {
			return !matchesCategoryPattern(t.object()) && !matchesCategoryPattern(t.subject());
		}

		private boolean matchesCategoryPattern(String uri) {
			if (!uri.startsWith(CATEGORY_PREFIX))
				return false;
			String category = uri.substring(CATEGORY_PREFIX.length());
			for (Pattern p : patterns) {
				if (p.matcher(category).matches())
					return true;
			}
			return false;
		}
	},
	ONTOLOGY_THRESHOLD {
		private final Predicate<Triple> pred = OntologyTriplePredicate.fromConfig(GraphConfig.config());

		@Override
		public boolean apply(Triple t) {
			return pred.apply(t);
		}
	},
	NON_ONTOLOGY {
		@Override
		public boolean apply(Triple t) {
			return !t.predicate().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
		}
	};

	private static final String CONFIG_TRIPLE_PREDICATE = "loading.filter.impl";

	static final String CATEGORY_PREFIX = "http://dbpedia.org/resource/Category:";
	static final String ONTOLOGY_PREFIX = "http://dbpedia.org/ontology/";

	/**
	 * Get a {@link Predicate} implementation from config.
	 * 
	 * @param config
	 *            A configuration object where the {@link TriplePredicate} is looked up.
	 * @return A {@link Predicate} instance.
	 */
	static Predicate<Triple> fromConfig(final Configuration config) {
		List<TriplePredicate> loadingTypes = EnumUtils.enumsfromConfig(TriplePredicate.class, config,
				CONFIG_TRIPLE_PREDICATE);

		if (loadingTypes == null || loadingTypes.isEmpty()) // no loading filter requested
			return ALL; // return dummy filter to use all triples

		return Predicates.and(loadingTypes);
	}
}
