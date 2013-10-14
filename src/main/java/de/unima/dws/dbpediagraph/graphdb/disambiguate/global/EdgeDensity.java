package de.unima.dws.dbpediagraph.graphdb.disambiguate.global;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;

import com.tinkerpop.blueprints.Graph;

import de.unima.dws.dbpediagraph.graphdb.GraphUtil;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.AbstractGlobalDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.GlobalDisambiguator;

/**
 * Edge density global connectivity measure implemented as described in Navigli&Lapata (2010).
 * 
 * @author Bernhard Schäfer
 * 
 */
public class EdgeDensity extends AbstractGlobalDisambiguator implements GlobalDisambiguator {

	@Override
	public Double globalConnectivityMeasure(Collection<String> senseAssignments, Graph sensegraph) {
		int totalEdges = GraphUtil.getNumberOfEdges(checkNotNull(sensegraph));
		checkArgument(totalEdges != 0, " the provided graph cannot contain 0 vertices.");
		int totalVertices = GraphUtil.getNumberOfVertices(sensegraph);
		// binomial (v over 2) === v * (v-1) / 2
		double edgesCompleteGraph = (totalVertices * (totalVertices - 1)) / 2.0;
		return totalEdges / edgesCompleteGraph;
	}

}
