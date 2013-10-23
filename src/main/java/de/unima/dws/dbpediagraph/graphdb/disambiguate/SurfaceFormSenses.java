package de.unima.dws.dbpediagraph.graphdb.disambiguate;

import java.util.Collection;

import org.dbpedia.spotlight.model.DBpediaResource;
import org.dbpedia.spotlight.model.SurfaceForm;

public class SurfaceFormSenses {
	private final SurfaceForm surfaceForm;
	private final Collection<DBpediaResource> senses;

	public SurfaceFormSenses(SurfaceForm surfaceForm, Collection<DBpediaResource> senses) {
		this.surfaceForm = surfaceForm;
		this.senses = senses;
	}

	public Collection<DBpediaResource> getSenses() {
		return senses;
	}

	public SurfaceForm getSurfaceForm() {
		return surfaceForm;
	}
}
