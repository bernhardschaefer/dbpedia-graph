package de.unima.dws.dbpediagraph.graphdb.model;

/**
 * A sense represents a potential meaning of a surface form of a word. It is represented by an URI.
 * 
 * @author Bernhard Schäfer
 * 
 */
public interface Sense {
	/**
	 * Return the unshortened URI of the sense.
	 * 
	 * @return the unshortened URI
	 */
	public String fullUri();
}
