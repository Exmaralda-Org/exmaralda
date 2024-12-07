package org.exmaralda.common.helpers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeHelper {
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


}
