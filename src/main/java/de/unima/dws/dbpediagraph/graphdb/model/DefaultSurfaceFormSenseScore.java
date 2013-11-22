package de.unima.dws.dbpediagraph.graphdb.model;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Immutable default {@link SurfaceFormSenseScore} implementation.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class DefaultSurfaceFormSenseScore implements SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense> {
	private final DefaultSurfaceForm surfaceForm;
	private final DefaultSense sense;
	private final double score;

	public DefaultSurfaceFormSenseScore(DefaultSurfaceForm surfaceForm, DefaultSense sense, double score) {
		this.surfaceForm = checkNotNull(surfaceForm, "Surface form cannot be null");
		this.sense = checkNotNull(sense, "Sense cannot be null");
		this.score = checkNotNull(score, "Score cannot be null");
	}

	@Override
	public int compareTo(SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense> o) {
		return Double.compare(score, o.score());
	}

	@Override
	public double score() {
		return score;
	}

	@Override
	public DefaultSense sense() {
		return sense;
	}

	@Override
	public DefaultSurfaceForm surfaceForm() {
		return surfaceForm;
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + surfaceForm.hashCode();
		result = 31 * result + sense.hashCode();
		long f = Double.doubleToLongBits(score);
		result = 31 * result + (int) (f ^ (f >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof DefaultSurfaceFormSenseScore))
			return false;
		DefaultSurfaceFormSenseScore senseScore = (DefaultSurfaceFormSenseScore) o;
		return score == senseScore.score && sense.equals(senseScore.sense)
				&& surfaceForm.equals(senseScore.surfaceForm);
	}

	@Override
	public String toString() {
		return new StringBuilder().append(surfaceForm.toString()).append(": ").append(sense.toString()).append(" --> ")
				.append(score).toString();
	}

}
