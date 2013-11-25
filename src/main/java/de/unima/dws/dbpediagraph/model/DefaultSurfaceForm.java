package de.unima.dws.dbpediagraph.model;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Immutable default {@link SurfaceForm} implementation. Can serve as a skeleton implementation of {@link SurfaceForm}
 * so that subclasses only need to additionally needed behavior.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class DefaultSurfaceForm implements SurfaceForm {
	private final String name;

	public DefaultSurfaceForm(String name) {
		this.name = checkNotNull(name, "Name must not be null");
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + name.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof DefaultSurfaceForm))
			return false;
		DefaultSurfaceForm surfaceForm = (DefaultSurfaceForm) o;
		return name.equals(surfaceForm.name);
	}

	@Override
	public String toString() {
		return name();
	}

}
