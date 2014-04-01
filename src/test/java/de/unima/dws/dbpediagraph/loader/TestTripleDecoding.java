package de.unima.dws.dbpediagraph.loader;

import static org.junit.Assert.assertEquals;

import java.util.*;
import java.util.Map.Entry;

import org.junit.Test;

/**
 * @author Bernhard Schäfer
 */
public class TestTripleDecoding {

	private static final Map<String, String> urisDumpToDecoded = new HashMap<>();
	private static final Map<String, String> urisSpotlightToDecoded = new HashMap<>();
	static {
		String dbr = "http://dbpedia.org/resource/";
		urisDumpToDecoded.put(dbr + "Company", dbr + "Company");
		urisDumpToDecoded.put(dbr + "Napol%C3%A9on_(1955_film)", dbr + "Napoléon_(1955_film)");
		urisDumpToDecoded.put(dbr + "War_in_Afghanistan_(2001%E2%80%93present)", dbr
				+ "War_in_Afghanistan_(2001–present)");

		urisSpotlightToDecoded.put(dbr + "Company_%28military_unit%29", dbr + "Company_(military_unit)");
		urisSpotlightToDecoded.put(dbr + "Wolfgang_Graf_von_Bl%C3%BCcher", dbr + "Wolfgang_Graf_von_Blücher");
		urisSpotlightToDecoded.put(dbr + "Sandra_Day_O%27Connor", dbr + "Sandra_Day_O'Connor");
		urisSpotlightToDecoded.put(dbr + "Goal%21_(film)", dbr + "Goal!_(film)");
		urisSpotlightToDecoded.put(dbr + "Napol%C3%A9on_%281955_film%29", dbr + "Napoléon_(1955_film)");
	}

	@Test
	public void testDecodingDumps() {
		for (Entry<String, String> entry : urisDumpToDecoded.entrySet()) {
			checkUri(entry.getKey(), entry.getValue());
		}
	}

	@Test
	public void testDecodingSpotlight() {
		for (Entry<String, String> entry : urisSpotlightToDecoded.entrySet()) {
			checkUri(entry.getKey(), entry.getValue());
		}
	}

	private static void checkUri(String fullUriEncoded, String fullUriDecoded) {
		Triple t = Triple.fromStringUris(fullUriEncoded, fullUriEncoded, fullUriEncoded);
		assertEquals(fullUriDecoded, t.subject());
		assertEquals(fullUriDecoded, t.predicate());
		assertEquals(fullUriDecoded, t.object());
	}
}
