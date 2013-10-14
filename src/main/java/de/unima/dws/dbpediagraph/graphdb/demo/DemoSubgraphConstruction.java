package de.unima.dws.dbpediagraph.graphdb.demo;

import java.awt.Dimension;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.oupls.jung.GraphJung;

import de.unima.dws.dbpediagraph.graphdb.GraphConfig;
import de.unima.dws.dbpediagraph.graphdb.GraphProvider;
import de.unima.dws.dbpediagraph.graphdb.GraphUtil;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstruction;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstructionNavigliOld;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;

public class DemoSubgraphConstruction {
	private static final int SIZE = 800;

	public static Set<Vertex> getTestVertices(Graph graph) {
		// http://en.wikipedia.org/wiki/Michael_I._Jordan
		// Michael I. Jordan is a leading researcher in machine learning and
		// artificial intelligence.

		String[] resources = new String[] { "Michael_I._Jordan", "Michael_Jordan", "Machine_learning",
				"Artificial_intelligence", "Basketball" };

		Set<Vertex> vertices = new HashSet<>();
		for (String resource : resources) {
			String uri = GraphConfig.DBPEDIA_RESOURCE_PREFIX + resource;
			vertices.add(GraphUtil.getVertexByUri(graph, uri));
		}

		return Collections.unmodifiableSet(vertices);
	}

	public static void main(String[] args) {
		Graph graph = GraphProvider.getDBpediaGraph();

		// SubgraphConstruction sc = new SubgraphConstructionNaive(graph);
		SubgraphConstruction sc = new SubgraphConstructionNavigliOld(graph, 3);
		Set<Vertex> vertices = getTestVertices(graph);
		Graph subGraph = sc.createSubgraph(vertices);
		// GraphPrinter.printGraphStatistics(subGraph);

		visualizeGraph(subGraph);

		subGraph.shutdown();
		graph.shutdown();
	}

	private static void visualizeGraph(Graph graph) {
		GraphJung<Graph> graphJung = new GraphJung<>(graph);
		// Layout<Vertex, Edge> layout = new CircleLayout<Vertex, Edge>(graphJung);
		Layout<Vertex, Edge> layout = new ISOMLayout<Vertex, Edge>(graphJung);
		layout.setSize(new Dimension(SIZE, SIZE));
		BasicVisualizationServer<Vertex, Edge> viz = new BasicVisualizationServer<Vertex, Edge>(layout);
		viz.setPreferredSize(new Dimension(SIZE, SIZE));

		Transformer<Vertex, String> vertexLabelTransformer = new Transformer<Vertex, String>() {
			@Override
			public String transform(Vertex vertex) {
				return vertex.getProperty(GraphConfig.URI_PROPERTY).toString();
			}
		};

		Transformer<Edge, String> edgeLabelTransformer = new Transformer<Edge, String>() {
			@Override
			public String transform(Edge edge) {
				String uriProp = edge.getProperty(GraphConfig.URI_PROPERTY);
				return uriProp != null ? uriProp : "";
			}
		};

		viz.getRenderContext().setEdgeLabelTransformer(edgeLabelTransformer);
		viz.getRenderContext().setVertexLabelTransformer(vertexLabelTransformer);

		JFrame frame = new JFrame("TinkerPop");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(viz);
		frame.pack();
		frame.setVisible(true);

	}
}
