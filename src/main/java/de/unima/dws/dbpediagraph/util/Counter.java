package de.unima.dws.dbpediagraph.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Single item counter that performs logging.
 * 
 * @author Bernhard Schäfer
 * 
 */
public class Counter {
	private static final Logger logger = LoggerFactory.getLogger(Counter.class);

	private final long tickRate;
	private final String name;

	// TODO change to nanoTime
	private final long startTime = System.currentTimeMillis();
	private long lastTickTime = startTime;
	private int count = 0;

	/**
	 * @param name
	 *            name of the counter used for logging
	 * @param tickRate
	 *            logging happens after every tickRate {@link #inc()} request
	 */
	public Counter(String name, long tickRate) {
		this.name = name;
		this.tickRate = tickRate;
	}

	public void inc() {
		if ((++count % tickRate) == 0) {
			long now = System.currentTimeMillis();
			long tickTimeDelta = now - lastTickTime;
			lastTickTime = now;
			logger.info(String.format("%s: %,d @ ~%.2f sec/%,d items.", name, count, tickTimeDelta / 1000.0, tickRate));
		}
	}

	public int count() {
		return count;
	}

	public void finish() {
		long totalTime = System.currentTimeMillis() - startTime;
		logger.info(String.format("DONE with %s (%,d items @ ~%.2f sec.) %n", name, count, totalTime / 1000.0));
	}

}
