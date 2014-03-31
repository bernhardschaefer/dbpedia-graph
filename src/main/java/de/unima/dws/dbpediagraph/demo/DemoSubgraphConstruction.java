package de.unima.dws.dbpediagraph.demo;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.*;
import com.tinkerpop.blueprints.oupls.jung.GraphJung;

import de.unima.dws.dbpediagraph.disambiguate.GraphDisambiguator;
import de.unima.dws.dbpediagraph.disambiguate.local.DegreeCentrality;
import de.unima.dws.dbpediagraph.graph.*;
import de.unima.dws.dbpediagraph.graph.GraphFactory;
import de.unima.dws.dbpediagraph.model.*;
import de.unima.dws.dbpediagraph.subgraph.*;
import de.unima.dws.dbpediagraph.util.FileUtils;
import de.unima.dws.dbpediagraph.weights.DummyEdgeWeights;
import de.unima.dws.dbpediagraph.weights.EdgeWeights;
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
	private static final Logger LOGGER = LoggerFactory.getLogger(DemoSubgraphConstruction.class);

	private static final String SENSES_FILE_NAME = "/demo/napoleon-1-sentence";

	private static final int MAX_DISTANCE = 1;
	private static final GraphType GRAPH_TYPE = GraphType.UNDIRECTED_GRAPH;

	private static final SubgraphConstructionSettings SETTINGS = new SubgraphConstructionSettings.Builder()
			.maxDistance(MAX_DISTANCE).graphType(GRAPH_TYPE).persistSubgraph(true)
			.persistSubgraphDirectory("/var/dbpedia-graphdb/subgraphs").build();

	// private static final EdgeWeights EDGE_WEIGHTS = EdgeWeightsFactory.fromEdgeWeightsType(EdgeWeightsType.JOINT_IC,
	// OccurrenceCounts.getDBpediaOccCounts());
	private static final EdgeWeights EDGE_WEIGHTS = DummyEdgeWeights.INSTANCE;

	private static final Collection<GraphDisambiguator<DefaultSurfaceForm, DefaultSense>> disambiguators;
	static {
		disambiguators = new ArrayList<>();

		// local
		// disambiguators.add(new BetweennessCentrality<DefaultSurfaceForm, DefaultSense>(GRAPH_TYPE, EDGE_WEIGHTS));
		disambiguators.add(new DegreeCentrality<DefaultSurfaceForm, DefaultSense>(GRAPH_TYPE, EDGE_WEIGHTS));
		// disambiguators.add(new HITSCentrality<DefaultSurfaceForm, DefaultSense>(GRAPH_TYPE, EDGE_WEIGHTS));
		// disambiguators.add(new KPPCentrality<DefaultSurfaceForm, DefaultSense>(GRAPH_TYPE, EDGE_WEIGHTS));
		// disambiguators.add(new PageRankCentrality<DefaultSurfaceForm, DefaultSense>(GRAPH_TYPE, EDGE_WEIGHTS));

		// global
		// disambiguators.add(new Compactness<DefaultSurfaceForm, DefaultSense>(GRAPH_TYPE, EDGE_WEIGHTS));
		// disambiguators.add(new EdgeDensity<DefaultSurfaceForm, DefaultSense>(GRAPH_TYPE, EDGE_WEIGHTS));
		// disambiguators.add(new GraphEntropy<DefaultSurfaceForm, DefaultSense>(GRAPH_TYPE, EDGE_WEIGHTS));
	}

	public static void main(String[] args) throws IOException, URISyntaxException {
		Map<DefaultSurfaceForm, List<DefaultSense>> wordsSensesString = FileUtils.parseSurfaceFormSensesFromFile(
				SENSES_FILE_NAME, DemoSubgraphConstruction.class, UriTransformer.DBPEDIA_RESOURCE_PREFIX);
		Graph graph = GraphFactory.getDBpediaGraph();
		demo(graph, wordsSensesString, disambiguators);
		graph.shutdown();
	}

	private static <T extends SurfaceForm, U extends Sense> void demo(Graph graph, Map<T, List<U>> surfaceFormsSenses,
			Collection<GraphDisambiguator<T, U>> disambiguators) {
		SubgraphConstruction sc = SubgraphConstructionFactory.newSubgraphConstruction(graph, SETTINGS);
		Graph subGraph = sc.createSubgraph(surfaceFormsSenses);

		for (GraphDisambiguator<T, U> d : disambiguators) {
			LOGGER.info("DISAMBIGUATOR {}", d);

			Map<T, List<SurfaceFormSenseScore<T, U>>> sfss = d.bestK(surfaceFormsSenses, subGraph, 3);
			for (List<SurfaceFormSenseScore<T, U>> sfScores : sfss.values()) {
				for (SurfaceFormSenseScore<T, U> senseScore : sfScores) {
					LOGGER.info(String.format("  %s (%.4f)", UriTransformer.shorten(senseScore.getSense().fullUri()),
							senseScore.getScore()));
				}
			}

			// List<SurfaceFormSenseScore<T, U>> senseScores = d.disambiguate(surfaceFormsSenses, subGraph);
			// for (SurfaceFormSenseScore<T, U> senseScore : senseScores)
			// LOGGER.info(String.format("  %s (%.2f)", UriTransformer.shorten(senseScore.getSense().fullUri()),
			// senseScore.getScore()));
		}

		visualizeGraph(subGraph, sc.getClass().getSimpleName() + " (max distance: " + MAX_DISTANCE + ")");

		subGraph.shutdown();
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
