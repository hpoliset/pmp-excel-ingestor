package org.srcm.heartfulness.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.srcm.heartfulness.constants.ExpressionConstants;

/**
 * Created by vsonnathi on 11/25/15.
 */
public class DateUtils {
	private static final Map<String, String> DATE_FORMAT_REGEXPS = new HashMap<String, String>() {
		{
			put("^\\d{8}$", "yyyyMMdd");
			put("^\\d{1,2}-\\s[a-z]{3}-\\d{2}$", "dd-MMM-yy");
			put("^\\d{1,2}\\.\\d{1,2}\\.\\d{4}$", "dd.MM.yyyy");
			put("^\\d{1,2}-\\d{1,2}-\\d{4}$", "dd-MM-yyyy");
			put("^\\d{4}-\\d{1,2}-\\d{1,2}$", "yyyy-MM-dd");
			put("^\\d{1,2}/\\d{1,2}/\\d{4}$", "dd/MM/yyyy");
			put("^\\d{4}/\\d{1,2}/\\d{1,2}$", "yyyy/MM/dd");
			put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}$", "dd MMM yyyy");
			put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}$", "dd MMMM yyyy");
			put("^\\d{12}$", "yyyyMMddHHmm");
			put("^\\d{8}\\s\\d{4}$", "yyyyMMdd HHmm");
			put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}$", "dd-MM-yyyy HH:mm");
			put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy-MM-dd HH:mm");
			put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}$", "MM/dd/yyyy HH:mm");
			put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy/MM/dd HH:mm");
			put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMM yyyy HH:mm");
			put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMMM yyyy HH:mm");
			put("^\\d{14}$", "yyyyMMddHHmmss");
			put("^\\d{8}\\s\\d{6}$", "yyyyMMdd HHmmss");
			put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd-MM-yyyy HH:mm:ss");
			put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy-MM-dd HH:mm:ss");
			put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "MM/dd/yyyy HH:mm:ss");
			put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy/MM/dd HH:mm:ss");
			put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMM yyyy HH:mm:ss");
			put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMMM yyyy HH:mm:ss");
			put("^\\d{1,2}-[a-zA-Z]{3,}-\\d{2,}$", "dd-MMM-yy");
		}
	};
	/*
	 * private static final Map<String, String> DATE_FORMAT_REGEXPS = new
	 * HashMap<String, String>() {{ put("^\\d{1,2}-[a-zA-Z]{3,}-\\d{2,}$",
	 * "dd-MMM-yy"); put("^\\d{1,2}\\.\\d{1,2}\\.\\d{4}$", "dd.MM.yyyy");
	 * put("^\\d{1,2}-\\d{1,2}-\\d{4}$", "dd-MM-yyyy"); }};
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DateUtils.class);

	/**
	 * Determine SimpleDateFormat pattern matching with the given date string.
	 * Returns null if format is unknown. You can simply extend DateUtils with
	 * more formats if needed.
	 * 
	 * @param dateString
	 *            The date string to determine the SimpleDateFormat pattern for.
	 * @return The matching SimpleDateFormat pattern, or null if format is
	 *         unknown.
	 * @see SimpleDateFormat
	 */
	public static String determineDateFormat(String dateString) {
		if (dateString == null || dateString.isEmpty()) {
			return null;
		}

		for (String regexp : DATE_FORMAT_REGEXPS.keySet()) {
			if (dateString.toLowerCase().matches(regexp)) {
				return DATE_FORMAT_REGEXPS.get(regexp);
			}
		}
		return null; // Unknown format.
	}

	public static Date parseDate(String dateString) throws ParseException {
		if (dateString == null || dateString.isEmpty()) {
			return null;
		}

		String dateFormat = determineDateFormat(dateString);
		if (dateFormat == null) {
			throw new ParseException("Could not parse:[" + dateString + "] - un supported format", 0);
		}

		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		sdf.setLenient(false);
		return sdf.parse(dateString);
	}

	public static java.sql.Date parseToSqlDate(String dateString) throws ParseException {
		Date parsed = parseDate(dateString);
		java.sql.Date sqlDate = new java.sql.Date(parsed.getTime());
		return sqlDate;
	}

	public static String getCurrentTimeInMilliSec(){
		Calendar cal = Calendar.getInstance();
		return new java.sql.Timestamp(cal.getTimeInMillis()).toString();
	}
	
	public static int countNumDaysBetweenTwoDates(String fromDate,String toDate) throws ParseException {
		Date from = new SimpleDateFormat(ExpressionConstants.DATE_FORMAT).parse(fromDate);
		Date to = new SimpleDateFormat(ExpressionConstants.DATE_FORMAT).parse(toDate);
		long difference = to.getTime()-from.getTime();
		return Math.round(difference/(1000*60*60*24));
	}

}
