package de.unima.dws.dbpediagraph.disambiguate.global;

import java.util.Map;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.oupls.jung.GraphJung;

import de.unima.dws.dbpediagraph.disambiguate.AbstractGlobalGraphDisambiguator;
import de.unima.dws.dbpediagraph.disambiguate.GlobalGraphDisambiguator;
import de.unima.dws.dbpediagraph.graph.GraphType;
import de.unima.dws.dbpediagraph.graph.Graphs;
import de.unima.dws.dbpediagraph.model.Sense;
import de.unima.dws.dbpediagraph.model.SurfaceForm;
import de.unima.dws.dbpediagraph.weights.EdgeWeights;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraDistance;
import edu.uci.ics.jung.algorithms.shortestpath.Distance;

/**
 * Compactness global connectivity measure implemented as described in Navigli&Lapata (2010).
 * 
 * @author Bernhard Schäfer
 * 
 */
public class Compactness<T extends SurfaceForm, U extends Sense> extends AbstractGlobalGraphDisambiguator<T, U>
		implements GlobalGraphDisambiguator<T, U> {

	public Compactness(GraphType graphType, EdgeWeights graphWeights) {
		super(graphType, graphWeights);
	}

	@Override
	public double globalConnectivityMeasure(Graph sensegraph) {
		Graphs.checkHasVertices(sensegraph);

		if (Graphs.hasNoEdges(sensegraph))
			return 0; // if there are no paths between sense vertices in the graph, there is no compactness

		GraphJung<Graph> graphJung = Graphs.asGraphJung(graphType, sensegraph);
		// Distance<Vertex> distances = new UnweightedShortestPath<>(graphJung);
		Distance<Vertex> distances = new DijkstraDistance<>(graphJung, edgeWeights);

		double sumDistances = 0;
		for (Vertex source : sensegraph.getVertices()) {
			Map<Vertex, Number> distancesFromSource = distances.getDistanceMap(source);
			for (Vertex target : sensegraph.getVertices()) {
				Number distance = distancesFromSource.get(target);
				sumDistances += distance == null ? 0.0 : distance.doubleValue();
			}
		}

		int totalVertices = Graphs.verticesCount(sensegraph);
		int min = totalVertices * (totalVertices - 1);
		int K = totalVertices; // TODO find out what k actually means
		int max = K * min;

		double compactness = (max - sumDistances) / (max - min);
		return compactness;
	}

}
