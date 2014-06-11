/**
 * Copyright  2006-2010 Soyatec
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 *
 * $Id$
 */
package org.soyatec.windowsazure.blob;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * The signedstart and signedexpiry fields must be expressed as UTC times and
 * must adhere to a valid ISO 8061 format. Supported ISO 8061 formats include
 * the following:
 * <ul>
 * <li>YYYY-MM-DD</li>
 * 
 * <li>YYYY-MM-DDThh:mmTZD</li>
 * 
 * <li>YYYY-MM-DDThh:mm:ssTZD</li>
 * </ul>
 * 
 * @author xiaowei.ye@soyatec.com
 * 
 */
public class DateTime {

	private String timeString;

	private final static SimpleDateFormat YYYY_MM_DD_FORMATTER = new SimpleDateFormat(
			"yyyy-MM-dd");

	private final static SimpleDateFormat YYYY_MMT_DD_T_HH_MM_FORMATTER = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm'Z'");

	private final static SimpleDateFormat YYYY_MMT_DD_T_HH_MM_SS_FORMATTER = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

	/**
	 * @return the timeString
	 */
	public String getTimeString() {
		return timeString;
	}

	/**
	 * Construct a new DateTime object with a date.
	 * @param date
	 */
	public DateTime(Date date) {
		this.timeString = YYYY_MMT_DD_T_HH_MM_SS_FORMATTER.format(date);
	}

	/**
	 * Construct a new DateTime object with a string date.
	 * @param string
	 */
	public DateTime(String string) {
		SimpleDateFormat[] formats = new SimpleDateFormat[] {
				YYYY_MM_DD_FORMATTER, YYYY_MMT_DD_T_HH_MM_FORMATTER,
				YYYY_MMT_DD_T_HH_MM_SS_FORMATTER };
		for (SimpleDateFormat format : formats) {
			try {
				format.parse(string);
				this.timeString = string;
				return;
			} catch (ParseException e) {
			}
		}
		throw new IllegalArgumentException();
	}

	/**
	 * Construct a new DateTime object with the integer year, month and date.
	 * The format is yyyy-MM-dd.
	 * @param year
	 * 			The year of the dataTime, should be an integer.
	 * @param month
	 * 			The month of the dataTime, should be an integer.
	 * @param date
	 * 			The date of the dataTime, should be an integer.
	 */
	public DateTime(int year, int month, int date) {
		Calendar calendar = createCalendar(year, month, date);
		this.timeString = YYYY_MM_DD_FORMATTER.format(calendar.getTime());
	}

	/**
	 * Construct a new DateTime object with the integer year, month, date, hour and minute.
	 * The format is YYYY-MM-DDThh:mmTZD.
	 * @param year
	 * 			The year of the dataTime, should be an integer.
	 * @param month
	 * 			The month of the dataTime, should be an integer.
	 * @param date
	 * 			The date of the dataTime, should be an integer.
	 * @param hour
	 * 			The hour of the dataTime, should be an integer.
	 * @param minute
	 * 			The minute of the dataTime, should be an integer.
	 */
	public DateTime(int year, int month, int date, int hour, int minute) {
		Calendar calendar = createCalendar(year, month, date, hour, minute);
		this.timeString = YYYY_MMT_DD_T_HH_MM_FORMATTER.format(calendar
				.getTime());
	}

	/**
	 * Construct a new DateTime object with the integer year, month, date, hour, minute and second.
	 * The format is YYYY-MM-DDThh:mm:ssTZD.
	 * @param year
	 * 			The year of the dataTime, should be an integer.
	 * @param month
	 * 			The month of the dataTime, should be an integer.
	 * @param date
	 * 			The date of the dataTime, should be an integer.
	 * @param hour
	 * 			The hour of the dataTime, should be an integer.
	 * @param minute
	 * 			The minute of the dataTime, should be an integer.
	 * @param second
	 * 			The second of the dataTime, should be an integer.
	 */
	public DateTime(int year, int month, int date, int hour, int minute,
			int second) {
		Calendar calendar = createCalendar(year, month, date, hour, minute,
				second);
		this.timeString = YYYY_MMT_DD_T_HH_MM_SS_FORMATTER.format(calendar
				.getTime());
	}

	/**
	 * Group integer year, month and date into the "yyyy-MM-dd" format.
	 * @param year
	 * 			The year of the dataTime, should be an integer.
	 * @param month
	 * 			The month of the dataTime, should be an integer.
	 * @param date
	 * 			The date of the dataTime, should be an integer.
	 * @return "yyyy-MM-dd" format string
	 */
	public static String create(int year, int month, int date) {
		Calendar dateTime = createCalendar(year, month, date);
		return YYYY_MM_DD_FORMATTER.format(dateTime.getTime());
	}

	/**
	 * Group integer year, month, date, hour and minute into the "YYYY-MM-DDThh:mmTZD" format.
	 * @param year
	 * 			The year of the dataTime, should be an integer.
	 * @param month
	 * 			The month of the dataTime, should be an integer.
	 * @param date
	 * 			The date of the dataTime, should be an integer.
	 * @param hour
	 * 			The hour of the dataTime, should be an integer.
	 * @param minute
	 * 			The minute of the dataTime, should be an integer.
	 * @return "YYYY-MM-DDThh:mmTZD" format string
	 */
	public static String create(int year, int month, int date, int hour,
			int minute) {
		Calendar dateTime = createCalendar(year, month, date, hour, minute);
		return YYYY_MMT_DD_T_HH_MM_FORMATTER.format(dateTime.getTime());
	}

	/**
	 * Group integer year, month, date, hour, minute and second into the "YYYY-MM-DDThh:mm:ssTZD" format.
	 * @param year
	 * 			The month of the dataTime, should be an integer.
	 * @param month
	 * 			The month of the dataTime, should be an integer.
	 * @param date
	 * 			The month of the dataTime, should be an integer.
	 * @param hour
	 * 			The month of the dataTime, should be an integer.
	 * @param minute
	 * 			The month of the dataTime, should be an integer.
	 * @param second
	 * 			The second of the dataTime, should be an integer.
	 * @return "YYYY-MM-DDThh:mm:ssTZD" format string
	 */
	public static String create(int year, int month, int date, int hour,
			int minute, int second) {
		Calendar dateTime = createCalendar(year, month, date, hour, minute,
				second);
		return YYYY_MMT_DD_T_HH_MM_SS_FORMATTER.format(dateTime.getTime());
	}

	/**
	 * Create calendar with integer year, month, date, hour, minute and second.
	 * @param year
	 * 			The month of the dataTime, should be an integer.
	 * @param month
	 * 			The month of the dataTime, should be an integer.
	 * @param date
	 * 			The month of the dataTime, should be an integer.
	 * @param hour
	 * 			The month of the dataTime, should be an integer.
	 * @param minute
	 * 			The month of the dataTime, should be an integer.
	 * @param second
	 * 			The second of the dataTime, should be an integer.
	 * @return Calendar object.
	 */
	private static Calendar createCalendar(int year, int month, int date,
			int hour, int minute, int second) {
		Calendar calendar = createCalendar(year, month, date, hour, minute);
		calendar.set(Calendar.SECOND, second);
		return calendar;
	}

	/**
	 * Create calendar with integer year, month, date, hour and minute.
	 * @param year
	 * 			The month of the dataTime, should be an integer.
	 * @param month
	 * 			The month of the dataTime, should be an integer.
	 * @param date
	 * 			The month of the dataTime, should be an integer.
	 * @param hour
	 * 			The month of the dataTime, should be an integer.
	 * @param minute
	 * 			The month of the dataTime, should be an integer.
	 * @return Calendar object.
	 */
	private static Calendar createCalendar(int year, int month, int date,
			int hour, int minute) {
		Calendar calendar = createCalendar(year, month, date);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		return calendar;
	}

	/**
	 * Create calendar with integer year, month, date.
	 * @param year
	 * 			The month of the dataTime, should be an integer.
	 * @param month
	 * 			The month of the dataTime, should be an integer.
	 * @param date
	 * 			The month of the dataTime, should be an integer.
	 * @return Calendar object.
	 */
	private static Calendar createCalendar(int year, int month, int date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setLenient(false);
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, date);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar;
	}
	
	/**
	 * Change dateTime obj to calendar. 
	 * @return Calendar object.
	 */
	public Calendar toCalendar(){
		SimpleDateFormat[] formats = new SimpleDateFormat[] {
				YYYY_MM_DD_FORMATTER, YYYY_MMT_DD_T_HH_MM_FORMATTER,
				YYYY_MMT_DD_T_HH_MM_SS_FORMATTER };
		for (SimpleDateFormat format : formats) {
			try {
				Date date = format.parse(this.timeString);
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				return calendar;
			} catch (ParseException e) {
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return timeString;
	}

}
