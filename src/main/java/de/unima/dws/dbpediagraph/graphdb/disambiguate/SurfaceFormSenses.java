package de.unima.dws.dbpediagraph.graphdb.disambiguate;

import java.util.Collection;

import org.dbpedia.spotlight.model.DBpediaResource;
import org.dbpedia.spotlight.model.SurfaceForm;
import org.dbpedia.spotlight.model.SurfaceFormOccurrence;

//TODO javadoc
public class SurfaceFormSenses {
	private final SurfaceFormOccurrence surfaceFormOccurrence;
	private final Collection<DBpediaResource> senses;

	public SurfaceFormSenses(SurfaceFormOccurrence surfaceFormOccurrence, Collection<DBpediaResource> senses) {
		this.surfaceFormOccurrence = surfaceFormOccurrence;
		this.senses = senses;
	}

	public Collection<DBpediaResource> getSenses() {
		return senses;
	}

	public SurfaceForm getSurfaceForm() {
		return surfaceFormOccurrence.surfaceForm();
	}

	public SurfaceFormOccurrence getSurfaceFormOccurrence() {
		return surfaceFormOccurrence;
	}
}
