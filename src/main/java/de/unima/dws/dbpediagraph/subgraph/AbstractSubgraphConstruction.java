package de.unima.dws.dbpediagraph.subgraph;

import java.util.Collection;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graph.*;
import de.unima.dws.dbpediagraph.util.CollectionUtils;

/**
 * {@link SubgraphConstruction} skeleton implementation. Subclasses only need to implement
 * {@link #dfs(Path, Set, Graph)}.
 * 
 * @author Bernhard Schäfer
 * 
 */
abstract class AbstractSubgraphConstruction implements SubgraphConstruction {
	protected static final Logger logger = LoggerFactory.getLogger(AbstractSubgraphConstruction.class);

	protected final SubgraphConstructionSettings settings;
	protected long traversedNodes;

	private final Graph graph;

	AbstractSubgraphConstruction(Graph graph, SubgraphConstructionSettings settings) {
		this.graph = graph;
		this.settings = settings;
	}

	@Override
	public Graph createSubSubgraph(Collection<Vertex> assignments, Set<Vertex> allSensesVertices) {
		long startTime = System.currentTimeMillis();

		// SubgraphConstructions.checkValidWordsSenses(graph, assignments);
		traversedNodes = 0;

		// initialize subgraph with all assignments
		Graph subsubgraph = GraphFactory.newInMemoryGraph();
		Graphs.addVerticesByIdIfNonExistent(subsubgraph, assignments);

		for (Vertex start : assignments) {
			Set<Vertex> targetSenses = CollectionUtils.remove(assignments, start);
			if (logger.isDebugEnabled())
				logger.debug("Starting DFS with vid: {}, uri: {}", start.getId(),
						start.getProperty(GraphConfig.URI_PROPERTY));
			Set<Vertex> stopVertices = Sets.difference(allSensesVertices, targetSenses);
			dfs(new Path(start), targetSenses, subsubgraph, stopVertices);
		}

		if (logger.isDebugEnabled())
			SubgraphConstructions.logSubgraphConstructionStats(logger, getClass(), subsubgraph, startTime,
					traversedNodes, settings.maxDistance);

		return subsubgraph;
	}

	@Override
	public Graph createSubgraph(Collection<Set<Vertex>> surfaceFormVertices) {
		long startTime = System.currentTimeMillis();

		SubgraphConstructions.checkValidWordsSenses(graph, surfaceFormVertices);
		traversedNodes = 0;

		Set<Vertex> allSenses = Sets.newHashSet(Iterables.concat(surfaceFormVertices));

		// initialize subgraph with all senses of all words
		Graph subGraph = GraphFactory.newInMemoryGraph();
		// Graphs.addVerticesByUrisOfVertices(subGraph, allSenses);
		Graphs.addVerticesByIdIfNonExistent(subGraph, allSenses);

		// perform a DFS for each sense trying to find path to senses of other
		// words
		for (Set<Vertex> senses : surfaceFormVertices) {
			Set<Vertex> targetSenses = Sets.difference(allSenses, senses);
			for (Vertex start : senses) {
				if (logger.isDebugEnabled())
					logger.debug("Starting DFS with vid: {}, uri: {}", start.getId(),
							start.getProperty(GraphConfig.URI_PROPERTY));
				Set<Vertex> stopVertices = Sets.difference(senses, Sets.newHashSet(start));
				dfs(new Path(start), targetSenses, subGraph, stopVertices);
			}
		}

		if (logger.isInfoEnabled())
			SubgraphConstructions.logSubgraphConstructionStats(logger, getClass(), subGraph, startTime, traversedNodes,
					settings.maxDistance);

		return subGraph;
	}

	/**
	 * Performs a DFS starting at the start vertex. The goal is to find all paths within the max distance to the other
	 * provided senses. Found paths are inserted into the subgraph.
	 * 
	 * @param start
	 *            the vertex the DFS starts with
	 * @param targets
	 *            the target senses
	 * @param subgraph
	 *            the subgraph where the paths are inserted to
	 * @param stopVertices
	 *            the senses vertices of the source surface form
	 */
	protected abstract void dfs(Path path, Set<Vertex> targets, Graph subgraph, Set<Vertex> stopVertices);

}
