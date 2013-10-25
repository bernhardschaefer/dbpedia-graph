package de.unima.dws.dbpediagraph.graphdb.disambiguate.spotlight;

import java.util.ArrayList;
import java.util.Collection;

import org.dbpedia.spotlight.model.DBpediaResource;
import org.dbpedia.spotlight.model.SurfaceFormOccurrence;

import de.unima.dws.dbpediagraph.graphdb.model.SurfaceFormSenses;

//TODO javadoc
public class DBpediaSurfaceFormSenses implements SurfaceFormSenses<DBpediaSurfaceForm, DBpediaSense> {
	private static Collection<DBpediaSense> convertToSenses(Collection<DBpediaResource> resources) {
		Collection<DBpediaSense> senses = new ArrayList<>();
		for (DBpediaResource resource : resources)
			senses.add(new DBpediaSense(resource));
		return senses;
	}

	private final DBpediaSurfaceForm surfaceForm;

	private final Collection<DBpediaSense> senses;

	public DBpediaSurfaceFormSenses(DBpediaSurfaceForm surfaceForm, Collection<DBpediaSense> senses) {
		this.surfaceForm = surfaceForm;
		this.senses = senses;
	}

	public DBpediaSurfaceFormSenses(SurfaceFormOccurrence sfOcc, Collection<DBpediaResource> candidates) {
		this.surfaceForm = new DBpediaSurfaceForm(sfOcc);
		this.senses = convertToSenses(candidates);
	}

	@Override
	public Collection<DBpediaSense> getSenses() {
		return senses;
	}

	@Override
	public DBpediaSurfaceForm getSurfaceForm() {
		return surfaceForm;
	}

}
