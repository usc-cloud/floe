package edu.usc.pgroup.floe.util;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Utils {
	public static String generateGMTTimeStamp()	
	{
		try
		{
			SimpleDateFormat gmtDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			gmtDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");	
			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
			timeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			Date date = new Date();
			return dateFormat.format(date)+ "T" + URLEncoder.encode(timeFormat.format(date),"UTF-8");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}
}
