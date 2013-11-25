package de.unima.dws.dbpediagraph.disambiguate.global;

import java.util.Map;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.oupls.jung.GraphJung;

import de.unima.dws.dbpediagraph.disambiguate.AbstractGlobalGraphDisambiguator;
import de.unima.dws.dbpediagraph.disambiguate.GlobalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graph.Graphs;
import de.unima.dws.dbpediagraph.model.*;
import de.unima.dws.dbpediagraph.subgraph.SubgraphConstructionSettings;
import edu.uci.ics.jung.algorithms.shortestpath.Distance;
import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;

/**
 * Compactness global connectivity measure implemented as described in Navigli&Lapata (2010).
 * 
 * @author Bernhard Schäfer
 * 
 */
public class Compactness<T extends SurfaceForm, U extends Sense> extends AbstractGlobalGraphDisambiguator<T, U>
		implements GlobalGraphDisambiguator<T, U> {

	public Compactness(SubgraphConstructionSettings subgraphConstructionSettings, ModelFactory<T, U> factory) {
		super(subgraphConstructionSettings, factory);
	}

	@Override
	public double globalConnectivityMeasure(Graph sensegraph) {
		Graphs.checkHasVertices(sensegraph);

		if (Graphs.edgesCount(sensegraph) == 0)
			return 0; // if there are paths between sense vertices in the graph, there is no compactness

		GraphJung<Graph> graphJung = Graphs.asGraphJung(subgraphConstructionSettings.graphType, sensegraph);
		Distance<Vertex> distances = new UnweightedShortestPath<>(graphJung);
		int sumDistances = 0;
		for (Vertex source : sensegraph.getVertices()) {
			Map<Vertex, Number> distancesFromSource = distances.getDistanceMap(source);
			for (Vertex target : sensegraph.getVertices()) {
				Number distance = distancesFromSource.get(target);
				sumDistances += distance == null ? 0 : distance.intValue();
			}
		}

		int totalVertices = Graphs.verticesCount(sensegraph);
		int min = totalVertices * (totalVertices - 1);
		int K = totalVertices; // TODO find out what k actually means
		int max = K * min;

		double compactness = ((double) (max - sumDistances)) / (max - min);
		return compactness;
	}

}
