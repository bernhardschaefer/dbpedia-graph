package de.unima.dws.dbpediagraph.graphdb.disambiguate.global;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.List;

import com.tinkerpop.blueprints.Graph;

import de.unima.dws.dbpediagraph.graphdb.Graphs;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.AbstractGlobalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.GlobalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.SurfaceFormSenseScore;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.SurfaceFormSenses;

/**
 * Edge density global connectivity measure implemented as described in Navigli&Lapata (2010).
 * 
 * @author Bernhard Schäfer
 * 
 */
public class EdgeDensity extends AbstractGlobalGraphDisambiguator implements GlobalGraphDisambiguator {

	@Override
	public List<SurfaceFormSenseScore> disambiguate(Collection<SurfaceFormSenses> surfaceFormsSenses, Graph subgraph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double globalConnectivityMeasure(Collection<String> senseAssignments, Graph sensegraph) {
		int totalEdges = Graphs.numberOfEdges(checkNotNull(sensegraph));
		checkArgument(totalEdges != 0, " the provided graph cannot contain 0 vertices.");
		int totalVertices = Graphs.numberOfVertices(sensegraph);
		// binomial (v over 2) === v * (v-1) / 2
		double edgesCompleteGraph = (totalVertices * (totalVertices - 1)) / 2.0;
		return totalEdges / edgesCompleteGraph;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}
