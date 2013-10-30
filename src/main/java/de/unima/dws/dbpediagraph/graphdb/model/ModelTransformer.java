package de.unima.dws.dbpediagraph.graphdb.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.Graphs;
import de.unima.dws.dbpediagraph.graphdb.util.FileUtils;

//TODO reorganise and javadoc
public class ModelTransformer {
	private static final Logger logger = LoggerFactory.getLogger(ModelTransformer.class);
	private static final double DEFAULT_SCORE = -1.0;

	public static <T extends SurfaceForm, U extends Sense> Map<T, List<SurfaceFormSenseScore<T, U>>> initializeScoresMapFromMap(
			Map<T, List<U>> surfaceFormsSenses, ModelFactory<T, U> factory) {
		Map<T, List<SurfaceFormSenseScore<T, U>>> sFSensesMap = new HashMap<>();
		for (Map.Entry<T, List<U>> sFSenses : surfaceFormsSenses.entrySet()) {
			List<SurfaceFormSenseScore<T, U>> sFSensesList = new ArrayList<SurfaceFormSenseScore<T, U>>();
			for (U sense : sFSenses.getValue()) {
				sFSensesList.add(factory.newSurfaceFormSenseScore(sFSenses.getKey(), sense, DEFAULT_SCORE));
			}
			sFSensesMap.put(sFSenses.getKey(), sFSensesList);
		}
		return sFSensesMap;
	}

	public static <T extends SurfaceForm, U extends Sense> List<U> sensesFromLine(String line, String uriPrefix,
			ModelFactory<T, U> factory) {
		List<U> senses = new ArrayList<>();
		String[] wordSenses = line.split(FileUtils.DELIMITER);
		for (int i = 0; i < wordSenses.length; i++) {
			String uri = uriPrefix + wordSenses[i];
			senses.add(factory.newSense(uri));
		}
		return senses;
	}

	public static <T extends SurfaceForm, U extends Sense> Map<T, List<U>> surfaceFormsSensesFromFile(Class<?> clazz,
			String fileName, String uriPrefix, ModelFactory<T, U> factory) throws IOException, URISyntaxException {
		Map<T, List<U>> wordsSenses = new HashMap<>();
		List<String> lines = FileUtils.readRelevantLinesFromFile(clazz, fileName);
		for (String line : lines)
			wordsSenses.put(factory.newSurfaceForm("test"), sensesFromLine(line, uriPrefix, factory));
		return wordsSenses;
	}

	public static <U extends Sense> List<U> transformVerticesToList(Collection<Vertex> vertices,
			ModelFactory<?, U> factory) {
		List<U> senses = new ArrayList<>();
		for (Vertex v : vertices) {
			senses.add(factory.newSense(v));
		}
		return senses;
	}

	public static <T extends SurfaceForm, U extends Sense> Map<T, List<U>> transformVerticesToMap(
			Collection<Collection<Vertex>> allWordsSenses, ModelFactory<T, U> factory) {
		Map<T, List<U>> surfaceFormSenses = new HashMap<>();
		for (Collection<Vertex> wordSenses : allWordsSenses)
			surfaceFormSenses.put(factory.newSurfaceForm("test"), transformVerticesToList(wordSenses, factory));
		return surfaceFormSenses;
	}

	public static Collection<Vertex> verticesFromSenses(Graph graph, Collection<? extends Sense> senses) {
		Collection<Vertex> vertices = new ArrayList<>(senses.size());
		for (Sense sense : senses) {
			Vertex v = Graphs.vertexByUri(graph, sense.fullUri());
			if (v != null)
				vertices.add(v);
			else
				logger.warn("No vertex found for uri {}", sense.fullUri());
		}
		return vertices;
	}

	public static Collection<Collection<Vertex>> wordsVerticesFromSenses(Graph graph,
			Map<? extends SurfaceForm, ? extends List<? extends Sense>> sFSenses) {
		Collection<Collection<Vertex>> wordVertices = new ArrayList<>();
		for (List<? extends Sense> senses : sFSenses.values()) {
			Collection<Vertex> vertices = verticesFromSenses(graph, senses);
			if (!vertices.isEmpty())
				wordVertices.add(vertices);
		}
		return wordVertices;
	}

}
