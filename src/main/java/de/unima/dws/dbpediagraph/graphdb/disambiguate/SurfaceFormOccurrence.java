package de.unima.dws.dbpediagraph.graphdb.disambiguate;

import java.util.Collection;

public class SurfaceFormOccurrence {
	private final String name;
	private final Collection<String> senses;

	public SurfaceFormOccurrence(String name, Collection<String> senses) {
		this.name = name;
		this.senses = senses;
	}

	public String getName() {
		return name;
	}

	public Collection<String> getSenses() {
		return senses;
	}

}
