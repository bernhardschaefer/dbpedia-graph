package de.unima.dws.dbpediagraph.graphdb.disambiguate.global;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.Graphs;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.AbstractGlobalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.GlobalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.SurfaceFormSenseScore;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.SurfaceFormSenses;
import de.unima.dws.dbpediagraph.graphdb.wrapper.GraphJungUndirected;
import edu.uci.ics.jung.algorithms.shortestpath.Distance;
import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;

/**
 * Compactness global connectivity measure implemented as described in Navigli&Lapata (2010).
 * 
 * @author Bernhard Schäfer
 * 
 */
public class Compactness extends AbstractGlobalGraphDisambiguator implements GlobalGraphDisambiguator {

	@Override
	public List<SurfaceFormSenseScore> disambiguate(Collection<SurfaceFormSenses> surfaceFormsSenses, Graph subgraph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double globalConnectivityMeasure(Collection<String> senseAssignments, Graph sensegraph) {
		Distance<Vertex> distances = new UnweightedShortestPath<>(new GraphJungUndirected(sensegraph));
		int sumDistances = 0;
		for (Vertex source : sensegraph.getVertices()) {
			Map<Vertex, Number> distancesFromSource = distances.getDistanceMap(source);
			for (Vertex target : sensegraph.getVertices()) {
				Number distance = distancesFromSource.get(target);
				sumDistances += distance == null ? 0 : distance.intValue();
			}
		}

		int totalVertices = Graphs.numberOfVertices(sensegraph);
		int min = totalVertices * (totalVertices - 1);
		int K = totalVertices; // TODO find out what k actually means
		int max = K * min;

		double compactness = ((double) (max - sumDistances)) / (max - min);
		return compactness;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

}
