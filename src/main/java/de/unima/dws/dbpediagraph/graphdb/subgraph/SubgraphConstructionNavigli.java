package de.unima.dws.dbpediagraph.graphdb.subgraph;

import java.util.Collection;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;

public class SubgraphConstructionNavigli implements SubgraphConstruction {

	@Override
	public Graph createSubgraph(Collection<Vertex> senses) {
		// V = vertices
		// List<Vertex> vertices = senses;
		// E = {}
		// List<Edge> edges = new LinkedList<Edge>();

		// perform a DFS from the vertex vertices(0) until we reach another
		// vertex from vertices(1-n).

		// add all edges and vertices on the path from both vertices
		// V = V.append(List<Vertex> path)
		// E = E.append(List<Edge> path)

		// backtrack to vertex before new vertex was discovered (path(n-2))

		return new TinkerGraph();
	}

}
