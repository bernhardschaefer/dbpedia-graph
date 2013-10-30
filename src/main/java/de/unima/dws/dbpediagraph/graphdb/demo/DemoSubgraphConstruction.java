package de.unima.dws.dbpediagraph.graphdb.demo;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.oupls.jung.GraphJung;

import de.unima.dws.dbpediagraph.graphdb.GraphConfig;
import de.unima.dws.dbpediagraph.graphdb.GraphFactory;
import de.unima.dws.dbpediagraph.graphdb.GraphType;
import de.unima.dws.dbpediagraph.graphdb.UriShortener;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.GraphDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.local.BetweennessCentrality;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.local.DegreeCentrality;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.local.HITSCentrality;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.local.KPPCentrality;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.local.PageRankCentrality;
import de.unima.dws.dbpediagraph.graphdb.model.DefaultModelFactory;
import de.unima.dws.dbpediagraph.graphdb.model.DefaultSense;
import de.unima.dws.dbpediagraph.graphdb.model.DefaultSurfaceForm;
import de.unima.dws.dbpediagraph.graphdb.model.ModelFactory;
import de.unima.dws.dbpediagraph.graphdb.model.ModelTransformer;
import de.unima.dws.dbpediagraph.graphdb.model.Sense;
import de.unima.dws.dbpediagraph.graphdb.model.SurfaceForm;
import de.unima.dws.dbpediagraph.graphdb.model.SurfaceFormSenseScore;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstruction;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstructionFactory;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstructionSettings;
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

	private static final int MAX_DISTANCE = 4;
	private static final GraphType GRAPH_TYPE = GraphType.DIRECTED_GRAPH;

	private static final ModelFactory<DefaultSurfaceForm, DefaultSense> factory = DefaultModelFactory.INSTANCE;
	private static final Collection<GraphDisambiguator<DefaultSurfaceForm, DefaultSense>> disambiguators;
	static {
		disambiguators = new ArrayList<>();
		disambiguators.add(new BetweennessCentrality<DefaultSurfaceForm, DefaultSense>(GRAPH_TYPE, factory));
		disambiguators.add(new DegreeCentrality<DefaultSurfaceForm, DefaultSense>(Direction.BOTH, factory));
		disambiguators.add(new HITSCentrality<DefaultSurfaceForm, DefaultSense>(GRAPH_TYPE, factory));
		disambiguators.add(new KPPCentrality<DefaultSurfaceForm, DefaultSense>(GRAPH_TYPE, factory));
		disambiguators.add(new PageRankCentrality<DefaultSurfaceForm, DefaultSense>(GRAPH_TYPE, factory));
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

	private static <T extends SurfaceForm, U extends Sense> void demo(Graph graph, Map<T, List<U>> surfaceFormsSenses,
			Collection<GraphDisambiguator<T, U>> disambiguators) {
		SubgraphConstruction sc = SubgraphConstructionFactory.newDefaultImplementation(graph,
				new SubgraphConstructionSettings().maxDistance(MAX_DISTANCE).graphType(GRAPH_TYPE));
		Graph subGraph = sc.createSubgraph(surfaceFormsSenses);

		for (GraphDisambiguator<T, U> d : disambiguators) {
			System.out.println(d);

			List<SurfaceFormSenseScore<T, U>> senseScores = d.disambiguate(surfaceFormsSenses, subGraph);
			for (SurfaceFormSenseScore<T, U> senseScore : senseScores)
				System.out.printf("  %s (%.2f)", UriShortener.shorten(senseScore.sense().fullUri()),
						senseScore.getScore());
			System.out.println();
		}

		visualizeGraph(subGraph, sc.getClass().getSimpleName() + " (max distance: " + MAX_DISTANCE + ")");

		subGraph.shutdown();
		graph.shutdown();
	}

	public static void main(String[] args) throws IOException, URISyntaxException {
		String sensesFileName = "/napoleon-sentence-test";
		// String sensesFileName = "/dbpedia-default-sentence-test";
		Map<DefaultSurfaceForm, List<DefaultSense>> wordsSensesString = ModelTransformer.surfaceFormsSensesFromFile(
				DemoSubgraphConstruction.class, sensesFileName, GraphConfig.DBPEDIA_RESOURCE_PREFIX, factory);

		Graph graph = GraphFactory.getDBpediaGraph();
		demo(graph, wordsSensesString, disambiguators);
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
