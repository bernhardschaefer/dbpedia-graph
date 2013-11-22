package de.unima.dws.dbpediagraph.graphdb.disambiguate.global;

import com.tinkerpop.blueprints.Graph;

import de.unima.dws.dbpediagraph.graphdb.Graphs;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.AbstractGlobalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.GlobalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.model.*;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstructionSettings;

/**
 * Edge density global connectivity measure implemented as described in Navigli&Lapata (2010).
 * 
 * @author Bernhard Schäfer
 * 
 */
public class EdgeDensity<T extends SurfaceForm, U extends Sense> extends AbstractGlobalGraphDisambiguator<T, U>
		implements GlobalGraphDisambiguator<T, U> {

	public EdgeDensity(SubgraphConstructionSettings subgraphConstructionSettings, ModelFactory<T, U> factory) {
		super(subgraphConstructionSettings, factory);
	}

	@Override
	public double globalConnectivityMeasure(Graph sensegraph) {
		Graphs.checkHasVertices(sensegraph);
		
		int totalVertices = Graphs.verticesCount(sensegraph);
		int totalEdges = Graphs.edgesCount(sensegraph);
		
		// binomial (v over 2) === v * (v-1) / 2
		double edgesCompleteGraph = (totalVertices * (totalVertices - 1)) / 2.0;
		return totalEdges / edgesCompleteGraph;
	}

}
