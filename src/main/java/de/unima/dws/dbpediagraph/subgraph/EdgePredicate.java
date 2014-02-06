package de.unima.dws.dbpediagraph.subgraph;

import java.util.List;

import org.apache.commons.configuration.Configuration;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.tinkerpop.blueprints.*;

import de.unima.dws.dbpediagraph.graph.Graphs;
import de.unima.dws.dbpediagraph.graph.UriTransformer;
import de.unima.dws.dbpediagraph.util.EnumUtils;

/**
 * Edge Predicate enum used for filtering edges during graph traversal in {@link SubgraphConstruction}.
 * @author Bernhard Schäfer
 *
 */
public enum EdgePredicate implements Predicate<Edge> {
	ALL {
		@Override
		public boolean apply(Edge input) {
			// e.getLabel().equals(GraphConfig.EDGE_LABEL);
			return true;
		}
	},
	NON_CATEGORY {
		// e.g. http://dbpedia.org/resource/Category:Financial_institutions
		private final String CATEGORY_PREFIX = UriTransformer.shorten("http://dbpedia.org/resource/Category:");

		@Override
		public boolean apply(Edge e) {
			return !Graphs.shortUriOfVertex(e.getVertex(Direction.IN)).startsWith(CATEGORY_PREFIX);
		}
	},
	NON_ONTOLOGY {
		private final String RDF_TYPE = UriTransformer.shorten("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");

		@Override
		public boolean apply(Edge e) {
			return !Graphs.shortUriOfEdge(e).equals(RDF_TYPE);
		}
	};

	public static Predicate<Edge> fromConfig(final Configuration config, String configKey) {
		List<EdgePredicate> edgePredicates = EnumUtils.enumsfromConfig(EdgePredicate.class, config, configKey);

		if (edgePredicates == null || edgePredicates.isEmpty()) // no edge filter requested
			return EdgePredicate.ALL; // return dummy filter to use all triples

		return Predicates.and(edgePredicates);
	}
}
