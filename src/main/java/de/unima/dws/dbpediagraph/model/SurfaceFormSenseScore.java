package de.unima.dws.dbpediagraph.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Comparator;

/**
 * Holder of a score for a {@link Sense} that corresponds to a {@link SurfaceForm}.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class SurfaceFormSenseScore<T extends SurfaceForm, U extends Sense> {

	private final T surfaceForm;
	private final U sense;
	private double score;

	public SurfaceFormSenseScore(T surfaceForm, U sense, double score) {
		this.surfaceForm = checkNotNull(surfaceForm, "Surface form cannot be null");
		this.sense = checkNotNull(sense, "Sense cannot be null");
		this.score = checkNotNull(score, "Score cannot be null");
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public U getSense() {
		return sense;
	}

	public T getSurfaceForm() {
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
		if (!(o instanceof SurfaceFormSenseScore<?, ?>))
			return false;
		SurfaceFormSenseScore<?, ?> senseScore = (SurfaceFormSenseScore<?, ?>) o;
		return score == senseScore.getScore() && sense.equals(senseScore.getSense())
				&& surfaceForm.equals(senseScore.getSurfaceForm());
	}

	@Override
	public String toString() {
		return new StringBuilder().append(surfaceForm.toString()).append(": ").append(sense.toString()).append(" --> ")
				.append(score).toString();
	}

	public static final Comparator<? super SurfaceFormSenseScore<?, ?>> SCORE_COMPARATOR = new Comparator<SurfaceFormSenseScore<?, ?>>() {
		@Override
		public int compare(SurfaceFormSenseScore<?, ?> left, SurfaceFormSenseScore<?, ?> right) {
			return Double.compare(left.getScore(), right.getScore());
		}
	};

	public static final Comparator<? super SurfaceFormSenseScore<?, ?>> DESCENDING_SCORE_COMPARATOR = new Comparator<SurfaceFormSenseScore<?, ?>>() {
		@Override
		public int compare(SurfaceFormSenseScore<?, ?> left, SurfaceFormSenseScore<?, ?> right) {
			return Double.compare(right.getScore(), left.getScore());
		}
	};

}
