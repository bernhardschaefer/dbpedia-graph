package de.unima.dws.dbpediagraph.demo;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.tinkerpop.blueprints.*;
import com.tinkerpop.blueprints.oupls.jung.GraphJung;

import de.unima.dws.dbpediagraph.disambiguate.GraphDisambiguator;
import de.unima.dws.dbpediagraph.disambiguate.global.*;
import de.unima.dws.dbpediagraph.disambiguate.local.*;
import de.unima.dws.dbpediagraph.graph.*;
import de.unima.dws.dbpediagraph.graph.GraphFactory;
import de.unima.dws.dbpediagraph.model.*;
import de.unima.dws.dbpediagraph.subgraph.*;
import de.unima.dws.dbpediagraph.util.FileUtils;
import de.unima.dws.dbpediagraph.weights.EdgeWeights;
import de.unima.dws.dbpediagraph.weights.EdgeWeightsFactory;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;

/**
 * {@link SubgraphConstruction} demo for visualizing the created subgraph.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class DemoSubgraphConstruction {

	private static final int MAX_DISTANCE = 2;
	private static final GraphType GRAPH_TYPE = GraphType.UNDIRECTED_GRAPH;
	// private static final GraphType GRAPH_TYPE = GraphType.DIRECTED_GRAPH;
	private static final EdgeWeights EDGE_WEIGHTS = EdgeWeightsFactory.dbpediaFromConfig(GraphConfig.config());
	private static final Predicate<Edge> EDGE_FILTER = Predicates.and(EdgePredicate.CATEGORY, EdgePredicate.ONTOLOGY);
	// private static final Predicate<Edge> EDGE_FILTER = EdgePredicate.DUMMY;

	private static final SubgraphConstructionSettings SETTINGS = new SubgraphConstructionSettings.Builder()
			.maxDistance(MAX_DISTANCE).graphType(GRAPH_TYPE).edgeFilter(EDGE_FILTER).build();

	private static final Collection<GraphDisambiguator<DefaultSurfaceForm, DefaultSense>> disambiguators;
	static {
		disambiguators = new ArrayList<>();

		// local
		disambiguators.add(new BetweennessCentrality<DefaultSurfaceForm, DefaultSense>(GRAPH_TYPE, EDGE_WEIGHTS));
		disambiguators.add(new DegreeCentrality<DefaultSurfaceForm, DefaultSense>(GRAPH_TYPE, EDGE_WEIGHTS));
		disambiguators.add(new HITSCentrality<DefaultSurfaceForm, DefaultSense>(GRAPH_TYPE, EDGE_WEIGHTS));
		disambiguators.add(new KPPCentrality<DefaultSurfaceForm, DefaultSense>(GRAPH_TYPE, EDGE_WEIGHTS));
		disambiguators.add(new PageRankCentrality<DefaultSurfaceForm, DefaultSense>(GRAPH_TYPE, EDGE_WEIGHTS));

		// global
		disambiguators.add(new Compactness<DefaultSurfaceForm, DefaultSense>(GRAPH_TYPE, EDGE_WEIGHTS));
		disambiguators.add(new EdgeDensity<DefaultSurfaceForm, DefaultSense>(GRAPH_TYPE, EDGE_WEIGHTS));
		disambiguators.add(new GraphEntropy<DefaultSurfaceForm, DefaultSense>(GRAPH_TYPE, EDGE_WEIGHTS));
	}

	private static <T extends SurfaceForm, U extends Sense> void demo(Graph graph, Map<T, List<U>> surfaceFormsSenses,
			Collection<GraphDisambiguator<T, U>> disambiguators) {
		SubgraphConstruction sc = SubgraphConstructionFactory.newSubgraphConstruction(graph, SETTINGS);
		Graph subGraph = sc.createSubgraph(surfaceFormsSenses);

		for (GraphDisambiguator<T, U> d : disambiguators) {
			System.out.println(d);

			List<SurfaceFormSenseScore<T, U>> senseScores = d.disambiguate(surfaceFormsSenses, subGraph);
			for (SurfaceFormSenseScore<T, U> senseScore : senseScores)
				System.out.printf("  %s (%.2f)", UriTransformer.shorten(senseScore.getSense().fullUri()),
						senseScore.getScore());
			System.out.println();
		}

		visualizeGraph(subGraph, sc.getClass().getSimpleName() + " (max distance: " + MAX_DISTANCE + ")");

		subGraph.shutdown();
	}

	public static void main(String[] args) throws IOException, URISyntaxException {
		// String sensesFileName = "/demo/dylan-sentence";
		String sensesFileName = "/demo/napoleon-sentence-test";
		// String sensesFileName = "/dbpedia-default-sentence-test";
		Map<DefaultSurfaceForm, List<DefaultSense>> wordsSensesString = FileUtils.parseSurfaceFormSensesFromFile(
				sensesFileName, DemoSubgraphConstruction.class, GraphConfig.DBPEDIA_RESOURCE_PREFIX);

		Graph graph = GraphFactory.getDBpediaGraph();
		demo(graph, wordsSensesString, disambiguators);
		graph.shutdown();
	}

	private static final Dimension SCREEN_DIMENSION;
	static {
		double percentageOfScreen = 0.95;
		int height = (int) (percentageOfScreen * GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getMaximumWindowBounds().height);
		int width = (int) (percentageOfScreen * GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getMaximumWindowBounds().width);
		SCREEN_DIMENSION = new Dimension(width, height);
	}

	private static void visualizeGraph(Graph graph, String frameTitle) {
		GraphJung<Graph> graphJung = new GraphJung<>(graph);
		// Layout<Vertex, Edge> layout = new CircleLayout<Vertex,
		// Edge>(graphJung);
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
				return Graphs.edgeToString(edge, EDGE_WEIGHTS);
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
