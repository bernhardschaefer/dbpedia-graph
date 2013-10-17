package de.unima.dws.dbpediagraph.graphdb.demo;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.oupls.jung.GraphJung;

import de.unima.dws.dbpediagraph.graphdb.GraphConfig;
import de.unima.dws.dbpediagraph.graphdb.GraphProvider;
import de.unima.dws.dbpediagraph.graphdb.GraphUtil;
import de.unima.dws.dbpediagraph.graphdb.UriShortener;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.Disambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.LocalDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.WeightedSense;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.local.BetweennessCentrality;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.local.DegreeCentrality;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.local.HITSCentrality;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.local.KPPCentrality;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.local.PageRankCentrality;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstruction;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstructionFactory;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstructionSettings;
import de.unima.dws.dbpediagraph.graphdb.util.CollectionUtils;
import de.unima.dws.dbpediagraph.graphdb.util.FileUtils;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;

public class DemoSubgraphConstruction {

	private static final int MAX_DISTANCE = 4;

	private static final Dimension SCREEN_DIMENSION;
	static {
		double percentageOfScreen = 0.95;
		int height = (int) (percentageOfScreen * GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getMaximumWindowBounds().height);
		int width = (int) (percentageOfScreen * GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getMaximumWindowBounds().width);
		SCREEN_DIMENSION = new Dimension(width, height);
	}

	public static void dbpediaDemo() throws IOException, URISyntaxException {
		Graph graph = GraphProvider.getDBpediaGraph();

		Collection<Collection<String>> wordsSensesString = FileUtils.readUrisFromFile(DemoSubgraphConstruction.class,
				"/napoleon-sentence-test", GraphConfig.DBPEDIA_RESOURCE_PREFIX);

		demo(graph, wordsSensesString);
	}

	private static void demo(Graph graph, Collection<Collection<String>> wordsSensesString) {
		Collection<Collection<Vertex>> wordsSenses = GraphUtil.getWordsVerticesByUri(graph, wordsSensesString);
		Collection<String> allSensesString = CollectionUtils.combine(wordsSensesString);

		SubgraphConstruction sc = SubgraphConstructionFactory.newDefaultImplementation(graph,
				new SubgraphConstructionSettings().maxDistance(MAX_DISTANCE));
		Graph subGraph = sc.createSubgraphFromSenses(wordsSenses);

		Disambiguator[] disambiguators = new LocalDisambiguator[] { new BetweennessCentrality(),
				new DegreeCentrality(Direction.BOTH), new HITSCentrality(), new KPPCentrality(),
				new PageRankCentrality(0.08) };
		for (Disambiguator d : disambiguators) {
			System.out.println(d);
			List<WeightedSense> weightedSenses = d.disambiguate(allSensesString, subGraph);
			Collections.sort(weightedSenses);
			for (WeightedSense sense : weightedSenses) {
				System.out.printf("  %s (%.2f)", UriShortener.shorten(sense.getSense()), sense.getWeight());
			}
			System.out.println();
		}

		visualizeGraph(subGraph, sc.getClass().getSimpleName() + " (max distance: " + MAX_DISTANCE + ")");

		subGraph.shutdown();
		graph.shutdown();
	}

	public static void main(String[] args) throws IOException, URISyntaxException {
		dbpediaDemo();
	}

	private static void visualizeGraph(Graph graph, String frameTitle) {
		GraphJung<Graph> graphJung = new GraphJung<>(graph);
		// Layout<Vertex, Edge> layout = new CircleLayout<Vertex, Edge>(graphJung);
		Layout<Vertex, Edge> layout = new ISOMLayout<Vertex, Edge>(graphJung);

		layout.setSize(SCREEN_DIMENSION);
		BasicVisualizationServer<Vertex, Edge> viz = new BasicVisualizationServer<Vertex, Edge>(layout);
		viz.setPreferredSize(SCREEN_DIMENSION);

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

		JFrame frame = new JFrame(frameTitle);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(viz);
		frame.pack();
		frame.setVisible(true);

	}
}
