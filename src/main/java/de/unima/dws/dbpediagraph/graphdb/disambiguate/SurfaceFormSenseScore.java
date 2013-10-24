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
		this.score = score;
	}

	@Override
	public int compareTo(SurfaceFormSenseScore o) {
		return Double.compare(o.score, score);
	}

	public double getScore() {
		return score;
	}

	public DBpediaResource getSense() {
		return sense;
	}

	public SurfaceForm getSurfaceForm() {
		return surfaceFormOccurrence.surfaceForm();
	}

	public SurfaceFormOccurrence getSurfaceFormOccurrence() {
		return surfaceFormOccurrence;
	}

	public void setScore(double score) {
		this.score = score;

	}

	public String uri() {
		return getSense().uri();
	}
}
