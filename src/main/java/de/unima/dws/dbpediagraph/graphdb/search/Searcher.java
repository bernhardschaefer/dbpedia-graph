package de.unima.dws.dbpediagraph.graphdb.search;

import java.util.List;
import java.util.Map;

import com.tinkerpop.blueprints.Graph;

import de.unima.dws.dbpediagraph.graphdb.model.Sense;
import de.unima.dws.dbpediagraph.graphdb.model.SurfaceForm;

public interface Searcher {

	<T extends SurfaceForm, U extends Sense> Map<T, U> search(Map<T, List<U>> surfaceFormsSenses, Graph subgraph, ConnectivityMeasureFunction<T, U> measureFunction);

}
