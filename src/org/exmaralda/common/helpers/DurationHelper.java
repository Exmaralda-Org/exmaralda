/**
 * 
 */
package org.exmaralda.common.helpers;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.exmaralda.coma.root.Ui;
import org.exmaralda.common.exceptions.MalformedDurationStringException;

/**
 * Helps converting millisecond-values to formatted strings and back
 * 
 * @author woerner
 * 
 */
public class DurationHelper {
	public static final int MILLISECONDS = 0;
	public static final int SECONDS = 1;
	public static final int MINUTES = 2;
	public static final int HOURS = 3;

	public static final String FORMAT_STRING = "[YYYYyDDDd] HH:MM:SS.TTT"; // TTT

	// =
	// milliseconds

	public static boolean validateDurationString(String ds) {
		return false;
	}

	// timeSpans for update-intervals
	public static HashMap<String, Long> timeSpans() {
		HashMap<String, Long> ts = new HashMap<String, Long>();
		ts.put("timespanNever", new Long("0"));
		ts.put("timespanDaily", new Long("86400000"));
		ts.put("timespanWeekly", new Long("604800000"));
		ts.put("timespanMonthly", new Long("2628000000"));
		return ts;
	}

	/**
	 * returns the time in milliseconds from the given formatted string
	 * 
	 * @param durationString
	 *            the string formatted as in <code>FORMAT_STRING</code>
	 * 
	 * @return the time in milliseconds as long value
	 * @throws MalformedDurationStringException
	 */
	public static long getDurationInMillis(String durationString)
			throws MalformedDurationStringException {
		String ds = durationString;
		long duration = 0;
		int tmp;
		// years
		try {
			if (ds.contains("y")) {
				tmp = Integer.parseInt(ds.split("y")[0]);
				ds = ds.substring(ds.indexOf("y") + 1);
				duration += TimeUnit.DAYS.toMillis(tmp * 365);
			}
			// days
			if (ds.contains("d")) {
				tmp = Integer.parseInt(ds.split("d")[0]);
				ds = ds.substring(ds.indexOf("d") + 2);
				duration += TimeUnit.DAYS.toMillis(tmp);
			}
			// hours
			tmp = Integer.parseInt(ds.split(":")[0]);
			duration += TimeUnit.HOURS.toMillis(tmp);
			ds = ds.substring(ds.indexOf(":") + 1);
			// minutes
			tmp = Integer.parseInt(ds.split(":")[0]);
			duration += TimeUnit.MINUTES.toMillis(tmp);
			ds = ds.substring(ds.indexOf(":") + 1);
			// seconds
			tmp = Integer.parseInt(ds.split("\\.")[0]);
			duration += TimeUnit.SECONDS.toMillis(tmp);
			ds = ds.substring(ds.indexOf(".") + 1);
			// milliseconds
			tmp = Integer.parseInt(ds);
			duration += tmp;
			return duration;
		} catch (Exception e) {
			throw new MalformedDurationStringException(durationString);
		}
	}

	/**
	 * creates a String formatted as in <code>FORMAT_STRING</code>
	 * 
	 * @param ms
	 *            the duration in milliseconds
	 * @return the formatted string
	 */
	public static String getDurationString(long ms, int precision) {
		String durString = "";
		String tmpString = "";
		long days = TimeUnit.MILLISECONDS.toDays(ms);
		if (days > 0) {
			if (days >= 365) {
				durString = (days / 365 + "y");
				durString += (days % 365 + "d ");
			} else {
				durString += days + "d ";
			}
			ms = ms - (TimeUnit.DAYS.toMillis(days));
		}
		tmpString = "00" + TimeUnit.MILLISECONDS.toHours(ms);
		durString += tmpString.substring(tmpString.length() - 2) + ":";
		if (TimeUnit.MILLISECONDS.toHours(ms) > 0) {
			ms = ms
					- (TimeUnit.HOURS.toMillis(TimeUnit.MILLISECONDS
							.toHours(ms)));
		}
		if (precision == HOURS) {
			durString += "00:00.000";
		} else {
			tmpString = "00" + TimeUnit.MILLISECONDS.toMinutes(ms);
			durString += tmpString.substring(tmpString.length() - 2) + ":";
			if (precision == MINUTES) {
				durString += "00.000";
			} else {
				if (TimeUnit.MILLISECONDS.toMinutes(ms) > 0) {
					ms = ms
							- (TimeUnit.MINUTES.toMillis(TimeUnit.MILLISECONDS
									.toMinutes(ms)));
				}
				tmpString = "00" + TimeUnit.MILLISECONDS.toSeconds(ms);
				durString += tmpString.substring(tmpString.length() - 2) + ".";
				if (precision == SECONDS) {
					durString += "000";
				} else {
					if (TimeUnit.MILLISECONDS.toSeconds(ms) > 0) {
						ms = ms
								- (TimeUnit.SECONDS
										.toMillis(TimeUnit.MILLISECONDS
												.toSeconds(ms)));
					}
					tmpString = "000" + TimeUnit.MILLISECONDS.toMillis(ms);
					durString += tmpString.substring(tmpString.length() - 3)
							+ "";
				}
			}
		}
		return durString;
	}

	/**
	 * returns a string to be used in html-renderers
	 * 
	 * @param ms
	 *            the duration in milliseconds
	 * @return the formatted string
	 */
	public static String getHTMLString(long ms, boolean showMS) {
		String str = getDurationString(ms, MILLISECONDS);
		str = str.replace("y", " " + Ui.getText("unit.years") + ", ");
		str = str.replace("d", " " + Ui.getText("unit.days") + ", ");
		if (showMS)
			str += "<small> (" + ms + "ms)</small>";
		return str;
	}
}
