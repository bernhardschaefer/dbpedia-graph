package de.unima.dws.dbpediagraph.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class for measuring timing performance
 * 
 * @author ponzetto
 * 
 */
public class Timer {

	/**
	 * The logger to be able to talk to the world
	 */
	static final protected Logger log = LoggerFactory.getLogger(Timer.class);

	/**
	 * Returns a string representation which summarizes the long {@code ms}
	 * field. The returned format is the following:
	 * <p>
	 * HHh:MMm:SSs
	 * <p>
	 * Example: if 2000 milliseconds are input, the string returned is
	 * 00h:00m:02s.
	 * 
	 * @return
	 */
	public static String getTimeFromMilliseconds(long ms) {
		int elapsedHours = (int) ((double) (ms) / (1000 * 60 * 60));
		int elapsedMinutes = (int) (((double) (ms - 60 * 60 * 1000 * elapsedHours)) / (1000 * 60));
		int elapsedSeconds = (int) (((double) (ms - 60 * 60 * 1000 * elapsedHours - 60 * 1000 * elapsedMinutes)) / (1000));

		return elapsedHours + "h : " + elapsedMinutes + "m : " + elapsedSeconds + "s";
	}

	/**
	 * Converts a given string representing a time format into milliseconds
	 * 
	 * @param time
	 *            a string representing time. This <b>must</b> be of the format
	 *            <p>
	 *            3h:4m:35s
	 *            </p>
	 *            Notes:
	 *            <ul>
	 *            <li>Colons are optional. So a date format like "6h24m49s" is
	 *            also an acceptable input.
	 *            <li>Spaces are accepted. So a date format like "6h 24m 49s" or
	 *            "6h 24m49s" are also acceptable inputs.
	 *            <li>Combinations of the above mentioned are accepted too. So a
	 *            date format like "6h 24m:49s" or "6h: 24m:49s" are also
	 *            acceptable inputs.
	 *            </ul>
	 * @return The equivalent in milliseconds of the input string time.
	 *         <p>
	 *         Example: getTimeInMilliseconds("0h:30:20s") returns
	 *         (30*60+20)*1000 = 1820000 milliseconds
	 *         </p>
	 */
	public static long getTimeInMilliseconds(String time) {
		long milliseconds = 0;

		time = time.replaceAll(":| ", "");
		time = time.toLowerCase();

		String REGEX = "(.*)h(.*)m(.*)s";
		Pattern p = Pattern.compile(REGEX);
		Matcher m = p.matcher(time);
		m.find();

		int hours = new Integer(m.group(1));
		int minutes = new Integer(m.group(2));
		int seconds = new Integer(m.group(3));

		milliseconds = 1000 * (hours * 3600 + minutes * 60 + seconds);

		return milliseconds;
	}

	/**
	 * Stores the time of the initial operation start.
	 */
	private long initialStartTime;

	/**
	 * Stores the time of the current operation start.
	 */
	private long lastStartTime;

	/**
	 * Use {@link Timer#getInstance()} to instantiate
	 * 
	 */
	public Timer() {
		setStartTime();
	}

	/**
	 * Returns a string representation of the time passed <b>since the initial
	 * start time</b>.
	 * <p>
	 * The returned format is the following:
	 * <p>
	 * HHh:MMm:SSs
	 * <p>
	 * Example: if 2000 milliseconds have passed, the string returned is
	 * 00h:00m:02s.
	 * 
	 * @return
	 */
	public String getElapsedTimeInString() {
		return Timer.getTimeFromMilliseconds(getTime());
	}

	/**
	 * Print how long the timed operation took. Milliseconds returned are
	 * measured from the initial start time.
	 * 
	 * @return Number of elapsed milliseconds
	 */
	public long getTime() {
		return System.currentTimeMillis() - initialStartTime;
	}

	/**
	 * Print how long the timed operation took.
	 * 
	 * @param str
	 *            Additional string to be printed out at end of timing
	 * @return Number of elapsed milliseconds
	 */
	public long getTime(final String str) {
		StringBuffer buffer = new StringBuffer();
		final long elapsed = System.currentTimeMillis() - initialStartTime;
		buffer.append("Time elapsed to ").append(str).append(": ").append(elapsed).append(" ms").toString();
		log.info(buffer.toString());
		return elapsed;
	}

	/**
	 * Returns a string representation of the time passed <b>since the last
	 * {@code tick()} operation</b>. This method invokes {@code tick()}, so the
	 * last ticked time will be updated.
	 * <p>
	 * The returned format is the following:
	 * <p>
	 * HHh:MMm:SSs
	 * <p>
	 * Example: if 2000 milliseconds have passed, the string returned is
	 * 00h:00m:02s.
	 * 
	 * @return
	 */
	public String getTimeInString() {
		return Timer.getTimeFromMilliseconds(tick());
	}

	/**
	 * Start the timing operation
	 */
	public void setStartTime() {
		this.initialStartTime = System.currentTimeMillis();
		this.lastStartTime = System.currentTimeMillis();
	}

	/**
	 * Computes how much time has passed. Time is measured from the last
	 * <code>tick</code> call, or the last call to <code>setStartTime</code> or
	 * when the class was loaded if there has been no previous call.
	 */
	public long tick() {
		final long time2 = System.currentTimeMillis();
		final long elapsed = time2 - lastStartTime;
		lastStartTime = time2;
		return elapsed;
	}

	/**
	 * Print how much time has passed. Time is measured from the last
	 * <code>tick</code> call, or the last call to <code>setStartTime</code> or
	 * when the class was loaded if there has been no previous call.
	 * 
	 * @param str
	 *            Prefix of string printed with time
	 * @return Number of elapsed milliseconds from tick (or start)
	 */
	public long tick(final String str) {
		StringBuffer buffer = new StringBuffer();
		final long time2 = System.currentTimeMillis();
		final long elapsed = time2 - lastStartTime;
		lastStartTime = time2;
		buffer.append("Time elapsed ").append(str).append(": ").append(elapsed).append(" ms").toString();
		log.info(buffer.toString());
		return elapsed;
	}
}
