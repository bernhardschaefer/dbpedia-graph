package de.unima.dws.dbpediagraph.util;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class to record graph loading metrics.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class LoadingMetrics {
	private static final Logger logger = LoggerFactory.getLogger(LoadingMetrics.class);

	private long startTime;

	/**
	 * Stores the time of when the last {@link #add(int, int)} has been called.
	 */
	private long lastAddTime;

	private final String metricName;

	private int validTriples;
	private int invalidTriples;
	private int totalTriples;

	private long timeDelta;

	/**
	 * Generate a new loading statistic and start the timer.
	 * 
	 * @param name
	 *            the name for the loading metrics (e.g. the file name or a global name)
	 */
	public LoadingMetrics(String name) {
		this.metricName = name;
		start();
	}

	/**
	 * Called for intermediate ticks.
	 */
	public void add(int tickValidTriples, int tickInvalidTriples) {
		this.validTriples += tickValidTriples;
		this.invalidTriples += tickInvalidTriples;
		long tickTotalTriples = tickValidTriples + tickInvalidTriples;
		this.totalTriples += tickTotalTriples;

		long addTime = System.currentTimeMillis();
		long tickTimeDelta = addTime - lastAddTime;
		lastAddTime = addTime;

		logger.info(String.format("triples: %,d (valid: %,d, invalid: %,d)  @ ~%.2f sec/%,d triples.", totalTriples,
				tickValidTriples, tickInvalidTriples, tickTimeDelta / 1000.0, tickTotalTriples));
	}

	/**
	 * Called after the loading job has finished. Stops the timer and stores the provided metrics, which can be
	 * retrieved using the {@link #toString()} method.
	 */
	public void finish(int validTriples, int invalidTriples) {
		this.validTriples = validTriples;
		this.invalidTriples = invalidTriples;

		this.totalTriples = validTriples + invalidTriples;

		this.timeDelta = System.currentTimeMillis() - startTime;

		logger.info(toString());
	}

	/**
	 * Called after the loading metric has finished its job. This method can be used for aggregated loading metrics,
	 * e.g. to retrieve the overall statistics for all files that have been parsed.
	 */
	public void finish(List<LoadingMetrics> metrics) {
		int sumValidTriples = 0;
		int sumInvalidTriples = 0;
		for (LoadingMetrics m : metrics) {
			sumValidTriples += m.validTriples;
			sumInvalidTriples += m.invalidTriples;
		}
		finish(sumValidTriples, sumInvalidTriples);
	}

	/**
	 * Start the timer.
	 */
	private void start() {
		startTime = System.currentTimeMillis();
		lastAddTime = startTime;
		logger.info("START parsing " + metricName);
	}

	/**
	 * Returns a string with all metrics that can e.g. be used for logging.
	 */
	@Override
	public String toString() {
		return String.format("DONE with %s. Overall time ~%.2f sec. %,d total triples (%,d valid, %,d invalid).%n",
				metricName, timeDelta / 1000.0, totalTriples, validTriples, invalidTriples);
	}

}
