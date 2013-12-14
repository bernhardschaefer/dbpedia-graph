package de.unima.dws.dbpediagraph.util;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

/**
 * Helper class to record graph loading metrics.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class LoadingMetrics {
	private static final Logger logger = LoggerFactory.getLogger(LoadingMetrics.class);

	private final String metricName;

	private int validTriples;
	private int invalidTriples;
	private int totalTriples;

	private final Stopwatch totalTime = Stopwatch.createStarted();
	private final Stopwatch tickTime = Stopwatch.createStarted();

	/**
	 * Generate a new loading statistic and start the timer.
	 * 
	 * @param name
	 *            the name for the loading metrics (e.g. the file name or a global name)
	 */
	public LoadingMetrics(String name) {
		this.metricName = name;
		logger.info("START parsing " + metricName);
	}

	/**
	 * Called for intermediate ticks.
	 */
	public void add(int tickValidTriples, int tickInvalidTriples) {
		this.validTriples += tickValidTriples;
		this.invalidTriples += tickInvalidTriples;
		long tickTotalTriples = tickValidTriples + tickInvalidTriples;
		this.totalTriples += tickTotalTriples;

		logger.info(String.format("triples: %,d (valid: %,d, invalid: %,d) @ %s / %,d triples.", totalTriples,
				tickValidTriples, tickInvalidTriples, tickTime, tickTotalTriples));
		tickTime.reset().start();
	}

	/**
	 * Called after the loading job has finished. Stops the timer and stores the provided metrics, which can be
	 * retrieved using the {@link #toString()} method.
	 */
	public void finish(int validTriples, int invalidTriples) {
		this.validTriples = validTriples;
		this.invalidTriples = invalidTriples;

		this.totalTriples = validTriples + invalidTriples;

		totalTime.stop();
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
	 * Returns a string with all metrics that can e.g. be used for logging.
	 */
	@Override
	public String toString() {
		return String.format("DONE with %s. Overall time %s / %,d total triples (%,d valid, %,d invalid).%n",
				metricName, totalTime, totalTriples, validTriples, invalidTriples);
	}

}
