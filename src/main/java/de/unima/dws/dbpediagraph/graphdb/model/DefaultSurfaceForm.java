package de.unima.dws.dbpediagraph.graphdb.model;

public class DefaultSurfaceForm implements SurfaceForm {
	private final String name;

	public DefaultSurfaceForm(String name) {
		this.name = name;
	}

	@Override
	public String name() {
		return name;
	}

}
