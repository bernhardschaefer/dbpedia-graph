package de.unima.dws.dbpediagraph.graphdb.model;

import java.util.Collection;

public class DefaultSurfaceFormSenses implements SurfaceFormSenses<DefaultSurfaceForm, DefaultSense> {
	private final DefaultSurfaceForm surfaceForm;
	private final Collection<DefaultSense> senses;

	public DefaultSurfaceFormSenses(DefaultSurfaceForm surfaceForm, Collection<DefaultSense> senses) {
		this.surfaceForm = surfaceForm;
		this.senses = senses;
	}

	@Override
	public Collection<DefaultSense> getSenses() {
		return senses;
	}

	@Override
	public DefaultSurfaceForm getSurfaceForm() {
		return surfaceForm;
	}

	@Override
	public String toString() {
		return surfaceForm.toString() + ": " + senses.toString();
	}

}
