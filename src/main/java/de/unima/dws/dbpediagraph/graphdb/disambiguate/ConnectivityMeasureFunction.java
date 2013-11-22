package de.unima.dws.dbpediagraph.graphdb.disambiguate;

import java.util.Map;

import com.tinkerpop.blueprints.Graph;

import de.unima.dws.dbpediagraph.graphdb.model.Sense;
import de.unima.dws.dbpediagraph.graphdb.model.SurfaceForm;

public interface ConnectivityMeasureFunction<T extends SurfaceForm, U extends Sense> {
	double getMeasure(Map<T, U> assignments, Graph subgraph);
}
