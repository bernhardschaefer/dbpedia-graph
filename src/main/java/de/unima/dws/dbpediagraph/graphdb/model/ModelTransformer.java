package de.unima.dws.dbpediagraph.graphdb.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.Graphs;
import de.unima.dws.dbpediagraph.graphdb.util.FileUtils;

//TODO reorganise and javadoc
public class ModelTransformer {
	private static final Logger logger = LoggerFactory.getLogger(ModelTransformer.class);

	public static <T extends SurfaceForm, U extends Sense> List<SurfaceFormSenseScore<T, U>> initializeScores(
			Collection<? extends SurfaceFormSenses<T, U>> surfaceFormsSenses, ModelFactory<T, U> factory) {
		List<SurfaceFormSenseScore<T, U>> senseScores = new ArrayList<>();
		for (SurfaceFormSenses<T, U> surfaceFormSenses : surfaceFormsSenses)
			for (U sense : surfaceFormSenses.getSenses())
				senseScores.add(factory.createInitialSurfaceFormSenseScore(surfaceFormSenses.getSurfaceForm(), sense));
		return senseScores;
	}

	public static <T extends SurfaceForm, U extends Sense> SurfaceFormSenses<T, U> surfaceFormSensesFromLine(
			String line, String uriPrefix, ModelFactory<T, U> factory) {
		Collection<U> senses = new ArrayList<>();
		String[] wordSenses = line.split(FileUtils.DELIMITER);
		for (int i = 0; i < wordSenses.length; i++) {
			String uri = uriPrefix + wordSenses[i];
			senses.add(factory.newSense(uri));
		}
		return transform(senses, "bla", factory);
	}

	public static <T extends SurfaceForm, U extends Sense> Collection<SurfaceFormSenses<T, U>> surfaceFormsSensesFromFile(
			Class<?> clazz, String fileName, String uriPrefix, ModelFactory<T, U> factory) throws IOException,
			URISyntaxException {
		Collection<SurfaceFormSenses<T, U>> wordsSenses = new ArrayList<>();
		List<String> lines = FileUtils.readRelevantLinesFromFile(clazz, fileName);
		for (String line : lines)
			wordsSenses.add(surfaceFormSensesFromLine(line, uriPrefix, factory));
		return wordsSenses;
	}

	private static <T extends SurfaceForm, U extends Sense> SurfaceFormSenses<T, U> transform(Collection<U> senses,
			String name, ModelFactory<T, U> factory) {
		return factory.newSurfaceFormSenses(senses, name);
	}

	private static <T extends SurfaceForm, U extends Sense> SurfaceFormSenses<T, U> transformVertex(
			Collection<Vertex> wordSenses, ModelFactory<T, U> factory) {
		Collection<U> senses = new ArrayList<>(wordSenses.size());
		for (Vertex v : wordSenses)
			senses.add(factory.newSense(v));
		return transform(senses, "bla", factory);
	}

	public static <T extends SurfaceForm, U extends Sense> Collection<SurfaceFormSenses<T, U>> transformVertices(
			Collection<Collection<Vertex>> allWordsSenses, ModelFactory<T, U> factory) {
		Collection<SurfaceFormSenses<T, U>> surfaceFormSenses = new ArrayList<>();
		for (Collection<Vertex> wordSenses : allWordsSenses)
			surfaceFormSenses.add(transformVertex(wordSenses, factory));
		return surfaceFormSenses;
	}

	public static <T extends SurfaceForm, U extends Sense> Collection<Vertex> verticesFromSenses(Graph graph,
			SurfaceFormSenses<T, U> surfaceFormSenses) {
		Collection<Vertex> vertices = new ArrayList<>(surfaceFormSenses.getSenses().size());
		for (Sense sense : surfaceFormSenses.getSenses()) {
			Vertex v = Graphs.vertexByUri(graph, sense.fullUri());
			if (v != null)
				vertices.add(v);
			else
				logger.warn("No vertex found for uri {}", sense.fullUri());
		}
		return vertices;
	}

	public static <T extends SurfaceForm, U extends Sense> Collection<Collection<Vertex>> wordsVerticesFromSenses(
			Graph graph, Collection<SurfaceFormSenses<T, U>> surfaceFormsSenses) {
		Collection<Collection<Vertex>> wordVertices = new ArrayList<>();
		for (SurfaceFormSenses<T, U> surfaceFormSenses : surfaceFormsSenses) {
			Collection<Vertex> vertices = verticesFromSenses(graph, surfaceFormSenses);
			if (!vertices.isEmpty())
				wordVertices.add(vertices);
		}
		return wordVertices;
	}

}
