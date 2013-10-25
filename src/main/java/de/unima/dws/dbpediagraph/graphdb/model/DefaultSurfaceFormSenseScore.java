package de.unima.dws.dbpediagraph.graphdb.model;

public class DefaultSurfaceFormSenseScore implements SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense> {
	private final DefaultSurfaceForm surfaceForm;
	private final DefaultSense sense;
	private double score;

	public DefaultSurfaceFormSenseScore(DefaultSurfaceForm surfaceForm, DefaultSense sense, double score) {
		this.surfaceForm = surfaceForm;
		this.sense = sense;
		this.score = score;
	}

	@Override
	public int compareTo(SurfaceFormSenseScore<DefaultSurfaceForm, DefaultSense> o) {
		return Double.compare(o.getScore(), score);
	}

	@Override
	public double getScore() {
		return score;
	}

	@Override
	public DefaultSense sense() {
		return sense;
	}

	@Override
	public void setScore(double score) {
		this.score = score;
	}

	@Override
	public DefaultSurfaceForm surfaceForm() {
		return surfaceForm;
	}

}
