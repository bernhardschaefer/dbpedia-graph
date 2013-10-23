package de.unima.dws.dbpediagraph.graphdb.disambiguate.spotlight;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.dbpedia.spotlight.disambiguate.Disambiguator;
import org.dbpedia.spotlight.exceptions.InputException;
import org.dbpedia.spotlight.exceptions.ItemNotFoundException;
import org.dbpedia.spotlight.exceptions.SearchException;
import org.dbpedia.spotlight.model.CandidateSearcher;
import org.dbpedia.spotlight.model.DBpediaResource;
import org.dbpedia.spotlight.model.DBpediaResourceOccurrence;
import org.dbpedia.spotlight.model.Provenance;
import org.dbpedia.spotlight.model.SurfaceForm;
import org.dbpedia.spotlight.model.SurfaceFormOccurrence;

/**
 * Graph based disambiguator compatible with the spotlight interface of {@link Disambiguator}.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class SpotlightDisambiguator extends AbstractGraphBasedDisambiguator implements Disambiguator {

	private static List<DBpediaResourceOccurrence> getRankedOccs(Collection<DBpediaResource> candidates,
			SurfaceFormOccurrence sfOcc) {
		List<DBpediaResourceOccurrence> rankedOccs = new LinkedList<DBpediaResourceOccurrence>();

		for (DBpediaResource resource : candidates) {
			DBpediaResourceOccurrence resultOcc = new DBpediaResourceOccurrence(resource, sfOcc.surfaceForm(),
					sfOcc.context(), sfOcc.textOffset(), Provenance.Annotation());
			rankedOccs.add(resultOcc);

		}
		return rankedOccs;
	}

	/**
	 * The only relevant method is {@link CandidateSearcher#getCandidates(SurfaceForm)}
	 */
	CandidateSearcher searcher;

	public SpotlightDisambiguator(CandidateSearcher searcher) {
		this.searcher = searcher;
	}

	@Override
	public List<DBpediaResourceOccurrence> disambiguate(List<SurfaceFormOccurrence> sfOccurrences)
			throws SearchException, InputException {

		Collection<Collection<DBpediaResourceOccurrence>> sfosCandidates = getSFOsCandidates(sfOccurrences);

		return null;
	}

	private Collection<Collection<DBpediaResourceOccurrence>> getSFOsCandidates(
			List<SurfaceFormOccurrence> sfOccurrences) throws SearchException {
		Collection<Collection<DBpediaResourceOccurrence>> sfosCandidates = new ArrayList<>(sfOccurrences.size());

		for (SurfaceFormOccurrence sfOcc : sfOccurrences)
			try {
				Collection<DBpediaResource> candidates = searcher.getCandidates(sfOcc.surfaceForm());
				List<DBpediaResourceOccurrence> rankedOccs = getRankedOccs(candidates, sfOcc);
				sfosCandidates.add(rankedOccs);
			} catch (ItemNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		return sfosCandidates;
	}

}
