package de.unima.dws.dbpediagraph.graphdb.disambiguate.spotlight;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.search.Explanation;
import org.dbpedia.spotlight.disambiguate.Disambiguator;
import org.dbpedia.spotlight.exceptions.InputException;
import org.dbpedia.spotlight.exceptions.ItemNotFoundException;
import org.dbpedia.spotlight.exceptions.SearchException;
import org.dbpedia.spotlight.model.DBpediaResource;
import org.dbpedia.spotlight.model.DBpediaResourceOccurrence;
import org.dbpedia.spotlight.model.SurfaceForm;
import org.dbpedia.spotlight.model.SurfaceFormOccurrence;
import org.dbpedia.spotlight.model.Text;

/**
 * Abstract graph-based {@link Disambiguator} that throws {@link UnsupportedOperationException} for all methods not
 * relevant for collective graph-based disambiguation.
 * 
 * @author Bernhard Schäfer
 * 
 */
public abstract class AbstractGraphBasedDisambiguator implements Disambiguator {

	@Override
	public int ambiguity(SurfaceForm sf) throws SearchException {
		throw new UnsupportedOperationException();
	}

	@Override
	public double averageIdf(Text context) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<DBpediaResourceOccurrence> bestK(SurfaceFormOccurrence sfOccurrence, int k) throws SearchException,
			ItemNotFoundException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int contextTermsNumber(DBpediaResource resource) throws SearchException {
		throw new UnsupportedOperationException();
	}

	@Override
	public abstract List<DBpediaResourceOccurrence> disambiguate(List<SurfaceFormOccurrence> sfOccurrences)
			throws SearchException, InputException;

	@Override
	public DBpediaResourceOccurrence disambiguate(SurfaceFormOccurrence sfOccurrence) throws SearchException,
			ItemNotFoundException, InputException {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Explanation> explain(DBpediaResourceOccurrence goldStandardOccurrence, int nExplanations)
			throws SearchException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String name() {
		return getClass().getSimpleName();
	}

	@Override
	public List<SurfaceFormOccurrence> spotProbability(List<SurfaceFormOccurrence> sfOccurrences) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int support(DBpediaResource res) throws SearchException {
		throw new UnsupportedOperationException();
	}

}
