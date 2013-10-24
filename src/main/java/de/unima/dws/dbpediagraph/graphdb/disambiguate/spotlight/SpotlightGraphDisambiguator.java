package de.unima.dws.dbpediagraph.graphdb.disambiguate.spotlight;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.dbpedia.spotlight.disambiguate.Disambiguator;
import org.dbpedia.spotlight.exceptions.InputException;
import org.dbpedia.spotlight.exceptions.ItemNotFoundException;
import org.dbpedia.spotlight.exceptions.SearchException;
import org.dbpedia.spotlight.model.CandidateSearcher;
import org.dbpedia.spotlight.model.DBpediaResource;
import org.dbpedia.spotlight.model.DBpediaResourceOccurrence;
import org.dbpedia.spotlight.model.SurfaceForm;
import org.dbpedia.spotlight.model.SurfaceFormOccurrence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.unima.dws.dbpediagraph.graphdb.GraphFactory;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.DisambiguatorHelper;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.GraphDisambiguator;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.SurfaceFormSenseScore;
import de.unima.dws.dbpediagraph.graphdb.disambiguate.SurfaceFormSenses;
import de.unima.dws.dbpediagraph.graphdb.subgraph.SubgraphConstruction;

/**
 * Graph based disambiguator compatible with the spotlight interface of {@link Disambiguator}.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class SpotlightGraphDisambiguator extends AbstractGraphBasedDisambiguator implements Disambiguator {

	private static List<DBpediaResourceOccurrence> unwrap(List<SurfaceFormSenseScore> senseScores) {
		List<DBpediaResourceOccurrence> resources = new ArrayList<>(senseScores.size());
		for (SurfaceFormSenseScore senseScore : senseScores)
			resources.add(new DBpediaResourceOccurrence(senseScore.getSense(), senseScore.getSurfaceForm(), senseScore
					.getSurfaceFormOccurrence().context(), senseScore.getSurfaceFormOccurrence().textOffset()));
		return resources;
	}

	private final GraphDisambiguator graphDisambiguator;

	private final SubgraphConstruction subgraphConstruction;

	/**
	 * The only relevant method is {@link CandidateSearcher#getCandidates(SurfaceForm)}
	 */
	CandidateSearcher searcher;

	private final Logger logger = LoggerFactory.getLogger(SpotlightGraphDisambiguator.class);

	public SpotlightGraphDisambiguator(GraphDisambiguator graphDisambiguator, SubgraphConstruction subgraphConstruction,
			CandidateSearcher searcher) {
		this.searcher = searcher;
		this.graphDisambiguator = graphDisambiguator;
		this.subgraphConstruction = subgraphConstruction;
	}

	@Override
	public List<DBpediaResourceOccurrence> disambiguate(List<SurfaceFormOccurrence> sfOccurrences)
			throws SearchException, InputException {

		Collection<SurfaceFormSenses> surfaceFormsSenses = getSFOsCandidates(sfOccurrences);

		Collection<Collection<Vertex>> wordsSenses = DisambiguatorHelper.wordsVerticesFromSenses(
				GraphFactory.getDBpediaGraph(), surfaceFormsSenses);
		Graph subgraph = subgraphConstruction.createSubgraph(wordsSenses);

		List<SurfaceFormSenseScore> results = graphDisambiguator.disambiguate(surfaceFormsSenses, subgraph);

		return unwrap(results);
	}

	private Collection<SurfaceFormSenses> getSFOsCandidates(List<SurfaceFormOccurrence> sfOccurrences)
			throws SearchException {
		Collection<SurfaceFormSenses> surfaceFormsSenses = new ArrayList<>(sfOccurrences.size());
		Collection<DBpediaResource> candidates;
		for (SurfaceFormOccurrence sfOcc : sfOccurrences) {
			try {
				candidates = searcher.getCandidates(sfOcc.surfaceForm());
			} catch (ItemNotFoundException e) {
				logger.warn("Error while trying to find candidates for {}", sfOcc.surfaceForm().name());
				logger.warn("Stack trace", e);
				candidates = Collections.emptyList();
			}
			surfaceFormsSenses.add(new SurfaceFormSenses(sfOcc, candidates));
		}
		return surfaceFormsSenses;
	}

}
