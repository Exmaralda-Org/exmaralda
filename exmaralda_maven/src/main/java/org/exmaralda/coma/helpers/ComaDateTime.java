package org.exmaralda.coma.helpers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import org.exmaralda.coma.datatypes.Period;
import org.exmaralda.common.helpers.DurationHelper;

public class ComaDateTime {

	/**
	 * creates a java.util.Date from a valid (yyyy-MM-ddTHH:mm:ss) XS:Date
	 * formatted string
	 * 
	 * @param xsDateTime
	 * @return
	 */
	public static Date dateFromXSDateTime(String xsDateTime) {
		try {
			DateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss");
			Date theDate = dateFormat.parse(xsDateTime);
			return theDate;
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * creates a XS:Date formatted string from a java.util.Date
	 * 
	 * @param date
	 * @return
	 */
	public static String xsDateTimeFromDate(Date date) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		return dateFormat.format(date);
	}

	/**
	 * just a String-to-long-conversion
	 * 
	 * @param xsDuration
	 * @return
	 */
	public static long msFromXSlong(String xsDuration) {
		return Long.valueOf(xsDuration).longValue();

	}

	public static long getTimeSpan(Period start, Period end) {
		if ((end.getPeriodStart() != null) && (start.getPeriodStart() != null)) {
			return (end.getPeriodStart().getTime())
					- (start.getPeriodStart().getTime());
		} else {
			return 0;
		}
	}

	public static long getTimeSpan(Date start, Date end, boolean rounded) {
		if ((start == null) || (end == null)) {
			return 0;
		} else {
			if (rounded) {
				start.setSeconds(0);
				end.setSeconds(0);
				start.setMinutes(0);
				end.setMinutes(0);
			}
			if ((end != null) && (start != null)) {
				return (end.getTime()) - (start.getTime());
			} else {
				return 0;
			}
		}
	}

	/**
	 * @param period
	 * @param commDate
	 * @return
	 */
	public static String getTimeSpanString(Period st, Period e,
			int spanDisplayType) {
		if ((e.getPeriodStart() != null) && (st.getPeriodStart() != null)
				&& (st != null) && (e != null)) {
			GregorianCalendar start = new GregorianCalendar();
			GregorianCalendar end = new GregorianCalendar();
			start.setTime(st.getPeriodStart());
			end.setTime(e.getPeriodStart());
			long timeInMs = start.getTimeInMillis() - end.getTimeInMillis();
			timeInMs = ((timeInMs < 0) ? timeInMs * -1 : timeInMs);
			return DurationHelper.getDurationString(timeInMs,
					DurationHelper.MILLISECONDS);

		} else {
			return null;
		}
	}
}
