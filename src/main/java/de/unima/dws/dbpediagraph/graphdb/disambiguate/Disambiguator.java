package de.unima.dws.dbpediagraph.graphdb.disambiguate;

import java.util.List;

import com.tinkerpop.blueprints.Vertex;

public interface Disambiguator {
	List<String> disambiguate(List<String> uris);

	List<Vertex> disambiguateVertices(List<Vertex> uris);

	List<WeightedUri> disambiguateWeighted(List<String> uris);
}
