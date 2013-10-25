package de.unima.dws.dbpediagraph.graphdb.model;

import java.util.Collection;

public interface SurfaceFormSenses<T extends SurfaceForm, U extends Sense> {

	Collection<U> getSenses();

	T getSurfaceForm();

}
