package de.unima.dws.dbpediagraph.graphdb.model;

/**
 * Immutable default {@link SurfaceForm} implementation.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class DefaultSurfaceForm implements SurfaceForm {
	private final String name;

	public DefaultSurfaceForm(String name) {
		this.name = name;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public String toString() {
		return name();
	}

}
