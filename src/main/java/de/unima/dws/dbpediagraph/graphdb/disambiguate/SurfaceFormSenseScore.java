package de.unima.dws.dbpediagraph.graphdb.disambiguate;

import org.dbpedia.spotlight.model.DBpediaResource;
import org.dbpedia.spotlight.model.SurfaceForm;

public class SurfaceFormSenseScore implements Comparable<SurfaceFormSenseScore> {
	private final SurfaceForm surfaceForm;
	private final DBpediaResource sense;
	private double score;

	public SurfaceFormSenseScore(SurfaceForm surfaceForm, DBpediaResource sense, double score) {
		this.surfaceForm = surfaceForm;
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

	public SurfaceForm getSurfaceForm() {
		return surfaceForm;
	}

	public void setScore(double score) {
		this.score = score;

	}

	public String uri() {
		return getSense().uri();
	}

	public DBpediaResource getSense() {
		return sense;
	}
}
