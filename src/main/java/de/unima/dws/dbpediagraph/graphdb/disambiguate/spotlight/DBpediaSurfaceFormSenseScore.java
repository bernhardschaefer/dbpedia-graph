package de.unima.dws.dbpediagraph.graphdb.disambiguate.spotlight;

import org.dbpedia.spotlight.model.DBpediaResource;
import org.dbpedia.spotlight.model.SurfaceFormOccurrence;

import de.unima.dws.dbpediagraph.graphdb.model.SurfaceFormSenseScore;

//TODO javadoc
public class DBpediaSurfaceFormSenseScore implements SurfaceFormSenseScore<DBpediaSurfaceForm, DBpediaSense> {
	private final DBpediaSurfaceForm surfaceForm;
	private final DBpediaSense sense;
	private double score;

	public DBpediaSurfaceFormSenseScore(DBpediaSurfaceForm dbpediaSurfaceForm, DBpediaSense sense) {
		this.surfaceForm = dbpediaSurfaceForm;
		this.sense = sense;

	}

	public DBpediaSurfaceFormSenseScore(SurfaceFormOccurrence surfaceFormOccurrence, DBpediaResource resource,
			double score) {
		this.surfaceForm = new DBpediaSurfaceForm(surfaceFormOccurrence);
		this.sense = new DBpediaSense(resource);
		this.setScore(score);
	}

	@Override
	public int compareTo(SurfaceFormSenseScore<DBpediaSurfaceForm, DBpediaSense> o) {
		return Double.compare(o.getScore(), getScore());
	}

	@Override
	public double getScore() {
		return score;
	}

	@Override
	public DBpediaSense sense() {
		return sense;
	}

	@Override
	public void setScore(double score) {
		this.score = score;
	}

	@Override
	public DBpediaSurfaceForm surfaceForm() {
		return surfaceForm;
	}

}
