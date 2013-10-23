package de.unima.dws.dbpediagraph.graphdb.disambiguate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.dbpedia.spotlight.model.DBpediaResource;
import org.dbpedia.spotlight.model.SurfaceForm;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.Graphs;

public class DisambiguatorHelper {

	public static List<SurfaceFormSenseScore> initializeScores(Collection<SurfaceFormSenses> surfaceFormsSenses) {
		List<SurfaceFormSenseScore> senseScores = new ArrayList<>();
		for (SurfaceFormSenses surfaceFormSenses : surfaceFormsSenses)
			for (DBpediaResource sense : surfaceFormSenses.getSenses())
				senseScores.add(new SurfaceFormSenseScore(surfaceFormSenses.getSurfaceForm(), sense, 0.0));
		return senseScores;
	}

	private static SurfaceFormSenses transformVertex(Collection<Vertex> wordSenses) {
		Collection<DBpediaResource> senses = new ArrayList<>(wordSenses.size());
		for (Vertex v : wordSenses)
			senses.add(new DBpediaResource(Graphs.uriOfVertex(v)));
		return new SurfaceFormSenses(new SurfaceForm("unknown name"), senses);
	}

	public static Collection<SurfaceFormSenses> transformVertices(Collection<Collection<Vertex>> allWordsSenses) {
		Collection<SurfaceFormSenses> surfaceFormSenses = new ArrayList<>();
		for (Collection<Vertex> wordSenses : allWordsSenses)
			surfaceFormSenses.add(transformVertex(wordSenses));
		return surfaceFormSenses;
	}

	public static Collection<Vertex> verticesFromSenses(Graph graph, SurfaceFormSenses surfaceFormSenses) {
		Collection<Vertex> vertices = new ArrayList<>(surfaceFormSenses.getSenses().size());
		for (DBpediaResource sense : surfaceFormSenses.getSenses())
			vertices.add(Graphs.vertexByUri(graph, sense.uri()));
		return vertices;
	}

	public static Collection<Collection<Vertex>> wordsVerticesFromSenses(Graph graph,
			Collection<SurfaceFormSenses> surfaceFormsSenses) {
		Collection<Collection<Vertex>> wordVertices = new ArrayList<>();
		for (SurfaceFormSenses surfaceFormSenses : surfaceFormsSenses) {
			Collection<Vertex> vertices = verticesFromSenses(graph, surfaceFormSenses);
			wordVertices.add(vertices);
		}
		return wordVertices;
	}

}
