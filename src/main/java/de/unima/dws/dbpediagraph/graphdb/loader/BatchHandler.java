package de.unima.dws.dbpediagraph.graphdb.loader;

import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.wrappers.batch.BatchGraph;

import de.unima.dws.dbpediagraph.graphdb.GraphConfig;
import de.unima.dws.dbpediagraph.graphdb.UriShortener;
import de.unima.dws.dbpediagraph.graphdb.filter.LoadingStatementFilter;

/**
 * Full blueprints compatible batch handler for creating a graph from RDF files. Uses {@link BatchGraph} and the
 * provided buffer size. See <a href="https://github.com/tinkerpop/blueprints/wiki/Batch-Implementation}"
 * >https://github.com/tinkerpop/blueprints/wiki/Batch-Implementation</a>
 * 
 * @author Bernhard Schäfer
 * 
 */
public class BatchHandler extends RDFHandlerVerbose {

	/** Log measures every TICK_SIZE time */
	private static final int TICK_SIZE = 1_000_000;

	/** The graph to persist to */
	private final Graph bgraph;

	/** Start logging time once the object has been created */
	private long tick = System.currentTimeMillis();

	/** the statement filter that decides if a statement is valid */
	private final LoadingStatementFilter statementFilter;

	private static final Logger logger = LoggerFactory.getLogger(BatchHandler.class);

	/**
	 * Initialize the handler with a graph object the statements should be added to.
	 */
	public BatchHandler(Graph graph, LoadingStatementFilter statementFilter) {
		this.bgraph = graph;
		this.statementFilter = statementFilter;
	}

	/**
	 * Adds a vertex with the uri as property to the graph if it does not exist yet.
	 */
	public Vertex addVertexByUriBatchIfNonExistent(String uri) {
		/*
		 * Neo4j ignores supplied ids, this means internal ids are autogenerated. However, batch graph has an id cache
		 * so that addVertex() works as expected. (https://github.com/tinkerpop/blueprints/wiki/Neo4j-Implementation)
		 */

		Vertex v = bgraph.getVertex(uri);
		if (v == null) {
			v = bgraph.addVertex(uri);
			v.setProperty(GraphConfig.URI_PROPERTY, uri);
		}
		return v;
	}

	@Override
	public void endRDF() throws RDFHandlerException {
	}

	@Override
	public void handleComment(String paramString) throws RDFHandlerException {
	}

	@Override
	public void handleNamespace(String paramString1, String paramString2) throws RDFHandlerException {
	}

	@Override
	public void handleStatement(Statement st) {
		if (!statementFilter.isValidStatement(st)) {
			invalidTriples++;
		} else {
			validTriples++;
			String subject = UriShortener.shorten(st.getSubject().stringValue());
			String predicate = UriShortener.shorten(st.getPredicate().stringValue());
			String object = UriShortener.shorten(st.getObject().stringValue());

			Vertex out = addVertexByUriBatchIfNonExistent(subject);
			Vertex in = addVertexByUriBatchIfNonExistent(object);
			Edge e = bgraph.addEdge(null, out, in, GraphConfig.EDGE_LABEL);
			e.setProperty(GraphConfig.URI_PROPERTY, predicate);
		}

		// logging metrics
		long totalTriples = validTriples + invalidTriples;
		if (totalTriples % TICK_SIZE == 0) {
			long timeDelta = (System.currentTimeMillis() - tick);
			logger.info(String.format("triples: %,d (valid: %,d, invalid: %,d)  @ ~%.2f sec/%,d triples.",
					totalTriples, validTriples, invalidTriples, timeDelta / 1000.0, TICK_SIZE));
			tick = System.currentTimeMillis();
		}

	}

	@Override
	public void startRDF() throws RDFHandlerException {
	}

}
