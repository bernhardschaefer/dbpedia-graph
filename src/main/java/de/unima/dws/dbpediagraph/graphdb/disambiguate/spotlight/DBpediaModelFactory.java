package de.unima.dws.dbpediagraph.graphdb.disambiguate.spotlight;

import java.util.Collection;

import org.dbpedia.spotlight.model.DBpediaResource;
import org.dbpedia.spotlight.model.SurfaceFormOccurrence;

import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.Graphs;
import de.unima.dws.dbpediagraph.graphdb.model.ModelFactory;
import de.unima.dws.dbpediagraph.graphdb.model.SurfaceFormSenseScore;
import de.unima.dws.dbpediagraph.graphdb.model.SurfaceFormSenses;

public enum DBpediaModelFactory implements ModelFactory<DBpediaSurfaceForm, DBpediaSense> {
	INSTANCE;

	@Override
	public SurfaceFormSenseScore<DBpediaSurfaceForm, DBpediaSense> createInitialSurfaceFormSenseScore(
			DBpediaSurfaceForm surfaceForm, DBpediaSense sense) {
		return new DBpediaSurfaceFormSenseScore(surfaceForm, sense);
	}

	@Override
	public DBpediaSense newSense(String uri) {
		return new DBpediaSense(new DBpediaResource(uri));
	}

	@Override
	public DBpediaSense newSense(Vertex v) {
		return newSense(Graphs.uriOfVertex(v));
	}

	@Override
	public SurfaceFormSenses<DBpediaSurfaceForm, DBpediaSense> newSurfaceFormSenses(Collection<DBpediaSense> senses,
			String name) {
		return new DBpediaSurfaceFormSenses(new DBpediaSurfaceForm(new SurfaceFormOccurrence(
				new org.dbpedia.spotlight.model.SurfaceForm(name), null, 0)), senses);
	}

}
