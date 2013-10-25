package de.unima.dws.dbpediagraph.graphdb.disambiguate;

import de.unima.dws.dbpediagraph.graphdb.model.Sense;
import de.unima.dws.dbpediagraph.graphdb.model.SurfaceForm;

public interface LocalGraphDisambiguator<T extends SurfaceForm, U extends Sense> extends GraphDisambiguator<T, U> {
}
