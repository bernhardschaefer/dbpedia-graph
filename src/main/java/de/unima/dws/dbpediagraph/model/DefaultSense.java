package de.unima.dws.dbpediagraph.model;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Immutable default {@link Sense} implementation. Can serve as a skeleton implementation of {@link Sense} so that
 * subclasses only need to additionally needed behavior.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class DefaultSense implements Sense {
	private final String fullUri;
	private final Double prior;

	public DefaultSense(String fullUri, Double prior) {
		this.fullUri = checkNotNull(fullUri, "Full uri cannot be null");
		this.prior = prior;
	}

	public DefaultSense(String fullUri) {
		this(fullUri, null);
	}

	@Override
	public String fullUri() {
		return fullUri;
	}

	@Override
	public Double prior() {
		return prior;
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + fullUri.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Sense))
			return false;
		Sense sense = (Sense) o;
		return fullUri.equals(sense.fullUri());
	}

	@Override
	public String toString() {
		return fullUri + " (prior: " + prior + " )";
	}

}
