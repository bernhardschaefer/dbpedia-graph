package de.unima.dws.dbpediagraph.graphdb.disambiguate;

/**
 * Local and global connectivity measures.
 * 
 * @author Bernhard Schäfer
 * 
 */
public enum ConnectivityMeasure {
	/* Local Measures */
	Degree, KPP, HITS, PR, Betweenness,
	/* Global Measures */
	Compactness, GraphEntropy, EdgeDensity
}
