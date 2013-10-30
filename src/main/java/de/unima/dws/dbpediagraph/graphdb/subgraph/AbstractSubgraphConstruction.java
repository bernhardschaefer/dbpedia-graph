package de.unima.dws.dbpediagraph.graphdb.subgraph;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.GraphConfig;
import de.unima.dws.dbpediagraph.graphdb.GraphFactory;
import de.unima.dws.dbpediagraph.graphdb.Graphs;
import de.unima.dws.dbpediagraph.graphdb.model.ModelTransformer;
import de.unima.dws.dbpediagraph.graphdb.model.Sense;
import de.unima.dws.dbpediagraph.graphdb.model.SurfaceForm;
import de.unima.dws.dbpediagraph.graphdb.util.CollectionUtils;

public abstract class AbstractSubgraphConstruction implements SubgraphConstruction {
	protected static final Logger logger = LoggerFactory.getLogger(AbstractSubgraphConstruction.class);

	protected final SubgraphConstructionSettings settings;
	protected long traversedNodes;

	private final Graph graph;

	public AbstractSubgraphConstruction(Graph graph, SubgraphConstructionSettings settings) {
		this.graph = graph;
		this.settings = settings;
	}

	@Override
	public Graph createSubgraph(Collection<Collection<Vertex>> wordsSenses) {
		long startTime = System.currentTimeMillis();

		SubgraphConstructions.checkValidWordsSenses(graph, wordsSenses);
		traversedNodes = 0;

		Collection<Vertex> allSenses = CollectionUtils.combine(wordsSenses);

		// initialize subgraph with all senses of all words
		Graph subGraph = GraphFactory.newInMemoryGraph();
		Graphs.addVerticesByUrisOfVertices(subGraph, allSenses);

		// perform a DFS for each sense trying to find path to senses of other
		// words
		for (Collection<Vertex> senses : wordsSenses) {
			Set<Vertex> otherSenses = CollectionUtils.removeAll(allSenses, senses);
			for (Vertex start : senses) {
				if (logger.isDebugEnabled())
					logger.debug("Starting DFS with vid: {}, uri: {}", start.getId(),
							start.getProperty(GraphConfig.URI_PROPERTY));
				dfs(new Path(start), otherSenses, subGraph);
			}
		}

		if (logger.isInfoEnabled())
			SubgraphConstructions.logSubgraphConstructionStats(logger, getClass(), subGraph, startTime, traversedNodes,
					settings.maxDistance);

		return subGraph;
	}

	@Override
	public Graph createSubgraph(Map<? extends SurfaceForm, ? extends List<? extends Sense>> surfaceFormSenses) {
		Collection<Collection<Vertex>> surfaceFormVertices = ModelTransformer.wordsVerticesFromSenses(graph,
				surfaceFormSenses);
		return createSubgraph(surfaceFormVertices);
	}

	/**
	 * Performs a DFS starting at the start vertex. The goal is to find all
	 * paths within the max distance to the other provided senses. Found paths
	 * are inserted into the subgraph.
	 * 
	 * @param start
	 *            the vertex the DFS starts with
	 * @param otherSenses
	 *            the target senses
	 * @param subGraph
	 *            the subgraph where the paths are inserted to
	 */
	protected abstract void dfs(Path path, Set<Vertex> targets, Graph subGraph);

}
