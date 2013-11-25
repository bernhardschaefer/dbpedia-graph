package de.unima.dws.dbpediagraph.graphdb.search;

/**
 * @author Bernhard Schäfer
 */
interface Scheduler {
	/**
	 * Get temperature based on time.
	 * 
	 * @param time
	 *            the time that the searcher is running already; can be represented by iteration count.
	 * @return the temperature
	 */
	public double getTemperature(int time);
}