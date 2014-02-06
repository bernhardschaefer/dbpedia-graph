package de.unima.dws.dbpediagraph.subgraph.filter;

import static org.junit.Assert.*;

import org.apache.commons.configuration.*;
import org.junit.*;

import com.google.common.base.Predicate;
import com.tinkerpop.blueprints.*;

import de.unima.dws.dbpediagraph.graph.*;
import de.unima.dws.dbpediagraph.graph.GraphFactory;
import de.unima.dws.dbpediagraph.subgraph.EdgePredicate;

public class TestEdgeFilters {

	static Graph GRAPH;

	static String MJ = "http://dbpedia.org/resource/Michael_Jordan";
	static String CATEGORY = "http://dbpedia.org/resource/Category:Baseball_players_from_New_York";
	static String ONTOLOGY = "http://dbpedia.org/ontology/BasketballPlayer";
	static String PROPERTY = "http://dbpedia.org/resource/Birmingham_Barons";

	static Vertex MJ_VERTEX;
	static Edge CATEGORY_EDGE;
	static Edge ONTOLOGY_EDGE;
	static Edge PROPERTY_EDGE;

	private static Configuration ontCatConfig;
	private static Configuration dummyConfig;
	private static final String configKey = "de.unima.dws.dbpediagraph.subgraph.edgeFilter";

	@BeforeClass
	public static void beforeClass() throws ConfigurationException {
		GRAPH = GraphFactory.newInMemoryGraph();

		MJ_VERTEX = createVertex(GRAPH, MJ);

		CATEGORY_EDGE = createEdge(GRAPH, MJ_VERTEX, "http://purl.org/dc/terms/subject", createVertex(GRAPH, CATEGORY));
		ONTOLOGY_EDGE = createEdge(GRAPH, MJ_VERTEX, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type",
				createVertex(GRAPH, ONTOLOGY));
		PROPERTY_EDGE = createEdge(GRAPH, MJ_VERTEX, "http://dbpedia.org/property/team", createVertex(GRAPH, PROPERTY));

		String prefix = "test-edge-filter/testconfig";
		String suffix = ".properties";
		ontCatConfig = new PropertiesConfiguration(prefix + "1" + suffix);
		dummyConfig = new PropertiesConfiguration(prefix + "2" + suffix);
	}

	@AfterClass
	public static void afterClass() {
		GRAPH.shutdown();
	}

	@Test
	public void testOntologyFilter() {
		Predicate<Edge> p = EdgePredicate.NON_ONTOLOGY;
		assertTrue(p.apply(CATEGORY_EDGE));
		assertFalse(p.apply(ONTOLOGY_EDGE));
		assertTrue(p.apply(PROPERTY_EDGE));
	}

	@Test
	public void testCategoryFilter() {
		Predicate<Edge> p = EdgePredicate.NON_CATEGORY;
		assertFalse(p.apply(CATEGORY_EDGE));
		assertTrue(p.apply(ONTOLOGY_EDGE));
		assertTrue(p.apply(PROPERTY_EDGE));
	}

	@Test
	public void testOntologyCategoryPredicateFromConfig() {
		Predicate<Edge> p = EdgePredicate.fromConfig(ontCatConfig, configKey);
		assertFalse(p.apply(CATEGORY_EDGE));
		assertFalse(p.apply(ONTOLOGY_EDGE));
		assertTrue(p.apply(PROPERTY_EDGE));
	}

	@Test
	public void testDummyPredicateFromConfig() {
		Predicate<Edge> p = EdgePredicate.fromConfig(dummyConfig, configKey);
		assertTrue(p.apply(CATEGORY_EDGE));
		assertTrue(p.apply(ONTOLOGY_EDGE));
		assertTrue(p.apply(PROPERTY_EDGE));
	}

	private static Edge createEdge(Graph g, Vertex sub, String pred, Vertex obj) {
		Edge e = g.addEdge(null, sub, obj, GraphConfig.EDGE_LABEL);
		e.setProperty(GraphConfig.URI_PROPERTY, UriTransformer.shorten(pred));
		return e;
	}

	private static Vertex createVertex(Graph graph, String fullUri) {
		Vertex vertex = graph.addVertex(null);
		vertex.setProperty(GraphConfig.URI_PROPERTY, UriTransformer.shorten(fullUri));
		return vertex;
	}

}
