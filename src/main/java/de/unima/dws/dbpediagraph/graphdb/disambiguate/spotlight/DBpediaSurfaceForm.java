package de.unima.dws.dbpediagraph.graphdb.disambiguate.spotlight;

import org.dbpedia.spotlight.model.SurfaceFormOccurrence;

import de.unima.dws.dbpediagraph.graphdb.model.SurfaceForm;

public class DBpediaSurfaceForm implements SurfaceForm {
	private final SurfaceFormOccurrence surfaceFormOccurrence;

	public DBpediaSurfaceForm(SurfaceFormOccurrence surfaceFormOccurrence) {
		this.surfaceFormOccurrence = surfaceFormOccurrence;
	}

	@Override
	public String name() {
		return getSurfaceFormOccurrence().surfaceForm().name();
	}

	public SurfaceFormOccurrence getSurfaceFormOccurrence() {
		return surfaceFormOccurrence;
	}

}
