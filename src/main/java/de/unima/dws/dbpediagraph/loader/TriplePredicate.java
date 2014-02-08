package de.unima.dws.dbpediagraph.loader;

import java.util.List;

import org.apache.commons.configuration.Configuration;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import de.unima.dws.dbpediagraph.graph.GraphConfig;
import de.unima.dws.dbpediagraph.util.*;

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
		@Override
		public boolean apply(Triple t) {
			return BlacklistTriplePredicate.getDefault().apply(t);
		}
	},
	/**
	 * Filter that yields all triple with DBpedia resources, ontologies, or yago classes as object. This includes the
	 * entire class hierarchy and classes such as foaf:Person.
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
	 * Filter that yields all triple with a DBpedia URI as object (http://dbpedia.org/*). This implementation is
	 * somewhat inconsistent since it partly includes the class hierarchy.
	 */
	DOMAIN {
		@Override
		public boolean apply(Triple t) {
			if (!t.object().startsWith("http://dbpedia.org/"))
				return false;
			return true;
		}
	},
	/**
	 * Filter that yields all triple with DBpedia resources as subject and object, which means the match the pattern
	 * http://dbpedia.org/resource/* .
	 */
	RESOURCE {
		@Override
		public boolean apply(Triple t) {
			if (!t.object().startsWith(GraphConfig.DBPEDIA_RESOURCE_PREFIX))
				return false;
			return true;
		}
	};

	private static final String CONFIG_TRIPLE_PREDICATE = "loading.filter.impl";

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
