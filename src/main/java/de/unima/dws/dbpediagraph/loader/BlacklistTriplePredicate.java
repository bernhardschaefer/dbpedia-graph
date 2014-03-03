package de.unima.dws.dbpediagraph.loader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;

import de.unima.dws.dbpediagraph.util.FileUtils;

/**
 * Custom blacklist triple filter which uses blacklists for subjects, predicates and objects.
 * 
 * @author Bernhard Schäfer
 */
class BlacklistTriplePredicate implements Predicate<Triple> {
	private static final Logger logger = LoggerFactory.getLogger(BlacklistTriplePredicate.class);

	private static final String CONFIG_BLACKLIST_FILES = "loading.filter.blacklist.files";

	private final Set<String> blacklist;

	BlacklistTriplePredicate(Set<String> blacklist) {
		this.blacklist = blacklist;
	}

	static BlacklistTriplePredicate fromConfig(Configuration config) {
		Set<String> blacklist = new HashSet<>();
		@SuppressWarnings("unchecked")
		List<String> blacklistFileNames = config.getList(CONFIG_BLACKLIST_FILES);
		for (String fileName : blacklistFileNames) {
			try {
				blacklist.addAll(FileUtils.readNonEmptyNonCommentLinesFromFile(BlacklistTriplePredicate.class, "/"
						+ fileName));
			} catch (IOException | URISyntaxException e) {
				logger.warn("Filter " + fileName + " could not be loaded.", e);
			}
		}
		return new BlacklistTriplePredicate(blacklist);
	}

	private static boolean isTripleUriInBlacklist(Triple t, Set<String> blacklist) {
		return blacklist.contains(t.subject()) || blacklist.contains(t.object());
	}

	@Override
	public boolean apply(Triple t) {
		return !isTripleUriInBlacklist(t, blacklist);
	}

}
