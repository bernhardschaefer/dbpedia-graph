package de.unima.dws.dbpediagraph.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

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

	private final Stopwatch totalTime = Stopwatch.createStarted();
	private final Stopwatch tickTime = Stopwatch.createStarted();

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
			logger.info(String.format("%s: %,d @ %s / %,d items.", name, count, tickTime, tickRate));
			tickTime.reset().start();
		}
	}

	public int count() {
		return count;
	}

	public void finish() {
		logger.info(String.format("DONE with %s (%,d items @ %s) %n", name, count, totalTime));
	}

}
