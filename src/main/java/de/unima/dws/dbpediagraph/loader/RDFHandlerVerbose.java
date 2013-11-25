package de.unima.dws.dbpediagraph.loader;

import org.openrdf.rio.RDFHandler;

import de.unima.dws.dbpediagraph.util.LoadingMetrics;

/**
 * Adds {@link LoadingMetrics} measures to RDFHandler.
 * 
 * @author Bernhard Schäfer
 * 
 */
public abstract class RDFHandlerVerbose implements RDFHandler {
	protected int validTriples = 0;
	protected int invalidTriples = 0;

	public int getInvalidTriples() {
		return invalidTriples;
	}

	public int getValidTriples() {
		return validTriples;
	}
}
