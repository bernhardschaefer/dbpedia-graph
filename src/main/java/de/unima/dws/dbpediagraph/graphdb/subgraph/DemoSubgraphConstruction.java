package de.unima.dws.dbpediagraph.graphdb.subgraph;

import java.awt.Dimension;
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
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;

public class DemoSubgraphConstruction {
	public static void main(String[] args) {
		Graph graph = GraphProvider.getInstance().getGraph();

		// SubgraphConstruction sc = new SubgraphConstructionNaive(graph);
		SubgraphConstruction sc = new SubgraphConstructionNavigli(graph);
		Set<Vertex> vertices = GraphUtil.getTestVertices(graph);
		Graph subGraph = sc.createSubgraph(vertices);
		// GraphPrinter.printGraphStatistics(subGraph);

		visualizeGraph(subGraph);

		graph.shutdown();
	}

	private static void visualizeGraph(Graph graph) {
		GraphJung graphJung = new GraphJung(graph);
		// Layout<Vertex, Edge> layout = new CircleLayout<Vertex, Edge>(graphJung);
		Layout<Vertex, Edge> layout = new ISOMLayout<Vertex, Edge>(graphJung);
		layout.setSize(new Dimension(900, 900));
		BasicVisualizationServer<Vertex, Edge> viz = new BasicVisualizationServer<Vertex, Edge>(layout);
		viz.setPreferredSize(new Dimension(650, 650));

		Transformer<Vertex, String> vertexLabelTransformer = new Transformer<Vertex, String>() {
			@Override
			public String transform(Vertex vertex) {
				return (String) vertex.getProperty(GraphConfig.URI_PROPERTY);
			}
		};

		Transformer<Edge, String> edgeLabelTransformer = new Transformer<Edge, String>() {
			@Override
			public String transform(Edge edge) {
				return edge.getProperty(GraphConfig.URI_PROPERTY);
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
