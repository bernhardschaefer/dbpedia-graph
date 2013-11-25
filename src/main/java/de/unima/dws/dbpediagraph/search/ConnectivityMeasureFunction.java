package de.unima.dws.dbpediagraph.search;

import java.util.Map;

import com.tinkerpop.blueprints.Graph;

import de.unima.dws.dbpediagraph.model.Sense;
import de.unima.dws.dbpediagraph.model.SurfaceForm;

public interface ConnectivityMeasureFunction<T extends SurfaceForm, U extends Sense> {
	public double getMeasure(Map<T, U> assignments, Graph subgraph);
}
