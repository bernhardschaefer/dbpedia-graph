package de.unima.dws.dbpediagraph.graphdb.model;

/**
 * Immutable default {@link Sense} implementation.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class DefaultSense implements Sense {
	private final String fullUri;

	public DefaultSense(String fullUri) {
		this.fullUri = fullUri;
	}

	@Override
	public String fullUri() {
		return fullUri;
	}

	@Override
	public String toString() {
		return fullUri();
	}

}
