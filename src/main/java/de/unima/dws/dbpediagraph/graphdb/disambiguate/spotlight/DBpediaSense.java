package de.unima.dws.dbpediagraph.graphdb.disambiguate.spotlight;

import org.dbpedia.spotlight.model.DBpediaResource;

import de.unima.dws.dbpediagraph.graphdb.model.Sense;

public class DBpediaSense implements Sense {

	private final DBpediaResource resource;

	public DBpediaSense(DBpediaResource resource) {
		this.resource = resource;
	}

	@Override
	public String fullUri() {
		return resource.getFullUri();
	}

	public DBpediaResource getResource() {
		return resource;
	}

}
