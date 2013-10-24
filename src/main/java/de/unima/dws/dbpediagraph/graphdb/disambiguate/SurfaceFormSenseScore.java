package de.unima.dws.dbpediagraph.graphdb.disambiguate;

import org.dbpedia.spotlight.model.DBpediaResource;
import org.dbpedia.spotlight.model.SurfaceForm;
import org.dbpedia.spotlight.model.SurfaceFormOccurrence;

//TODO javadoc
public class SurfaceFormSenseScore implements Comparable<SurfaceFormSenseScore> {
	private final SurfaceFormOccurrence surfaceFormOccurrence;
	private final DBpediaResource sense;
	private double score;

	public SurfaceFormSenseScore(SurfaceFormOccurrence surfaceForm, DBpediaResource sense, double score) {
		this.surfaceFormOccurrence = surfaceForm;
		this.sense = sense;
		this.setScore(score);
	}

	@Override
	public int compareTo(SurfaceFormSenseScore o) {
		return Double.compare(o.getScore(), getScore());
	}

	public String fullUri() {
		return sense().getFullUri();
	}

	public double getScore() {
		return score;
	}

	public DBpediaResource sense() {
		return sense;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public SurfaceForm surfaceForm() {
		return surfaceFormOccurrence.surfaceForm();
	}

	public SurfaceFormOccurrence surfaceFormOccurrence() {
		return surfaceFormOccurrence;
	}
}
