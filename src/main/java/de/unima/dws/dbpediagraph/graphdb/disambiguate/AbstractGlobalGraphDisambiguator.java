package de.unima.dws.dbpediagraph.graphdb.disambiguate;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.model.ModelTransformer;
import de.unima.dws.dbpediagraph.graphdb.model.Sense;
import de.unima.dws.dbpediagraph.graphdb.model.SurfaceForm;
import de.unima.dws.dbpediagraph.graphdb.model.SurfaceFormSenseScore;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstruction;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstructionFactory;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstructionSettings;
import de.unima.dws.dbpediagraph.graphdb.util.CollectionUtils;

/**
 * Skeleton class which eases the implementation of {@link GraphDisambiguator}.
 * Subclasses only need to implement
 * {@link #globalConnectivityMeasure(Map, Graph)}.
 * 
 * @author Bernhard Schäfer
 * 
 */
public abstract class AbstractGlobalGraphDisambiguator<T extends SurfaceForm, U extends Sense> implements
		GlobalGraphDisambiguator<T, U> {
	private final SubgraphConstructionSettings subgraphConstructionSettings;

	public AbstractGlobalGraphDisambiguator(SubgraphConstructionSettings subgraphConstructionSettings) {
		this.subgraphConstructionSettings = subgraphConstructionSettings;
	}

	@Override
	public Map<T, List<SurfaceFormSenseScore<T, U>>> bestK(Map<T, List<U>> surfaceFormsSenses, Graph subgraph, int k) {
		// TODO think about how to simulate this
		throw new UnsupportedOperationException("bestK not supported for global disambiguators");
	}

	@Override
	public List<SurfaceFormSenseScore<T, U>> disambiguate(Map<T, List<U>> surfaceFormsSenses, Graph subgraph) {
		// // Example allWordSenses = {{drink1,drink2},{milk1,milk2,milk3}}
		//
		// // iteration over all possible sense assignments , e.g.:
		// // 1st iteration: [drink1,milk1]
		// // 2nd iteration: [drink1,milk2]
		// // 3rd iteration: [drink1,milk3]
		// // 4th iteration: [drink2,milk1]
		// // ...
		//
		// // calculate global connectivity measure and add to result list
		//
		// // TODO implement genetic and simulated annealing functionality
		return null;
	}

	@Override
	public double globalConnectivityMeasure(Collection<Vertex> surfaceFormSenseAssigments, Graph subgraph) {
		SubgraphConstruction sensegraphConstruction = SubgraphConstructionFactory.newDefaultImplementation(subgraph,
				subgraphConstructionSettings);
		Graph sensegraph = sensegraphConstruction.createSubgraph(CollectionUtils.split(surfaceFormSenseAssigments));
		double score = globalConnectivityMeasure(sensegraph);
		sensegraph.shutdown();
		return score;
	}

	@Override
	public abstract double globalConnectivityMeasure(Graph sensegraph);

	@Override
	public double globalConnectivityMeasure(Map<T, U> surfaceFormSenseAssigments, Graph subgraph) {
		Collection<Vertex> vertices = ModelTransformer
				.verticesFromSenses(subgraph, surfaceFormSenseAssigments.values());
		return globalConnectivityMeasure(vertices, subgraph);
	}

}
