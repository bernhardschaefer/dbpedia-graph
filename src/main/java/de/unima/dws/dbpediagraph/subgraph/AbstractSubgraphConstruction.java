package de.unima.dws.dbpediagraph.subgraph;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graph.*;
import de.unima.dws.dbpediagraph.model.*;
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
	public Graph createSubgraph(Collection<Set<Vertex>> surfaceFormsVertices) {
		Stopwatch stopwatch = Stopwatch.createStarted();

		SubgraphConstructions.checkValidWordsSenses(graph, surfaceFormsVertices);
		traversedNodes = 0;

		Set<Vertex> allCandidates = Sets.newHashSet(Iterables.concat(surfaceFormsVertices));

		// initialize subgraph with all senses of all words
		Graph subgraph = GraphFactory.newInMemoryGraph();
		// Graphs.addVerticesByUrisOfVertices(subGraph, allSenses);
		Graphs.addVerticesByIdIfNonExistent(subgraph, allCandidates);

		// perform a DFS for each sense trying to find path to candidates of other surface forms
		for (Set<Vertex> sfCandidates : surfaceFormsVertices) {
			Set<Vertex> targetCandidates = Sets.difference(allCandidates, sfCandidates);
			for (Vertex start : sfCandidates) {
				if (logger.isDebugEnabled())
					logger.debug("Starting DFS with vid: {}, uri: {}", start.getId(),
							start.getProperty(GraphConfig.URI_PROPERTY));
				Set<Vertex> stopVertices = Sets.difference(sfCandidates, Sets.newHashSet(start));
				dfs(new Path(start), targetCandidates, subgraph, stopVertices);
			}
		}

		if (logger.isInfoEnabled())
			SubgraphConstructions.logSubgraphConstructionStats(logger, getClass(), subgraph, stopwatch, traversedNodes,
					settings.maxDistance);

		if (settings.persistSubgraph)
			GraphExporter.persistGraphInDirectory(subgraph, true, settings.persistSubgraphDirectory);

		return subgraph;
	}

	@Override
	public Graph createSubgraph(Map<? extends SurfaceForm, ? extends List<? extends Sense>> surfaceFormSenses) {
		Collection<Set<Vertex>> surfaceFormVertices = ModelToVertex.verticesFromSurfaceFormSenses(graph,
				surfaceFormSenses, true);
		return createSubgraph(surfaceFormVertices);
	}

	@Override
	public Graph createSubSubgraph(Collection<Vertex> assignments, Set<Vertex> allSensesVertices) {
		Stopwatch stopwatch = Stopwatch.createStarted();

		traversedNodes = 0;

		// initialize subgraph with all assignments
		Graph subsubgraph = GraphFactory.newInMemoryGraph();
		Graphs.addVerticesByIdIfNonExistent(subsubgraph, assignments);

		for (Vertex start : assignments) {
			Set<Vertex> targetSenses = CollectionUtils.remove(assignments, start);
			if (logger.isTraceEnabled())
				logger.trace("Starting DFS with vid: {}, uri: {}", start.getId(),
						start.getProperty(GraphConfig.URI_PROPERTY));
			Set<Vertex> stopVertices = Sets.difference(allSensesVertices, targetSenses);
			dfs(new Path(start), targetSenses, subsubgraph, stopVertices);
		}

		if (logger.isTraceEnabled())
			SubgraphConstructions.logSubgraphConstructionStats(logger, getClass(), subsubgraph, stopwatch,
					traversedNodes, settings.maxDistance);

		return subsubgraph;
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
