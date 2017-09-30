package com.hy.util;

import com.alibaba.druid.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期转换类 转换一个 java.util.Date 对象到一个字符串以及 一个字符串到一个 java.util.Date 对象.
 */
public class DateUtil {
	public static final long SECOND = 1000;

	public static final long MINUTE = SECOND * 60;

	public static final long HOUR = MINUTE * 60;

	public static final long DAY = HOUR * 24;

	public static final long WEEK = DAY * 7;

	public static final long MONTH = DAY * 30;

	public static final long YEAR = DAY * 365;

	public static final String TYPE_DATE = "date";

	public static final String TYPE_TIME = "time";

	public static final String TYPE_DATETIME = "datetime";

	/**
	 * 模式:yyyy-MM-dd HH:mm
	 */
	// public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm";

	// public static final String PATTERN_DATETIME_ = "yyyyMMddHHmmss";

	/**
	 * 模式:yyyy-MM-dd
	 */
	public static final String PATTERN_DATE = "yyyy-MM-dd";
	public static final String[] TYPE_ALL = { TYPE_DATE, TYPE_DATETIME, TYPE_TIME };
	private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 将字符串转换为Date类型
	 * 
	 * @param strDate
	 * @param pattern
	 *            格式
	 * @return
	 * @throws ParseException
	 */
	public static Date convertStringToDate(String strDate, String pattern) {
		if (StringUtils.isEmpty(strDate))
			return null;
		if (StringUtils.isEmpty(pattern))
			pattern = DEFAULT_PATTERN;
		SimpleDateFormat df = new SimpleDateFormat(pattern);
		try {
			return df.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * 将Date转换为字符串
	 * 
	 * @param aDate
	 * @param pattern
	 *            default:yyyy-MM-dd HH:mm:ss 格式
	 * @return
	 */
	public static String convertDateTimeToString(Date aDate, String pattern) {
		if (aDate == null)
			return null;
		if (StringUtils.isEmpty(pattern))
			pattern = DEFAULT_PATTERN;
		SimpleDateFormat df = new SimpleDateFormat(pattern);
		return df.format(aDate);
	}

	/**
	 *
	 * @param aDate
	 * @param pattern
	 *            default: yyyy-MM-dd
	 * @return
	 */
	public static String convertDateToString(Date aDate, String pattern) {
		if (aDate == null)
			return null;
		if (StringUtils.isEmpty(pattern))
			pattern = PATTERN_DATE;
		SimpleDateFormat df = new SimpleDateFormat(pattern);
		return df.format(aDate);
	}

	/**
	 * string格式化为string
	 */
	public static String stringFormat(String time, String pattern) {
		Date date = null;
		try {
			date = new SimpleDateFormat(DEFAULT_PATTERN).parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return convertDateTimeToString(date, pattern);
	}

	/**
	 * 将日期、时间合并成长整型数据
	 * 
	 * @param date
	 *            日期
	 * @param time
	 *            时间
	 * @return
	 */
	public static long getDateTimeNumber(Date date, Date time) {
		Calendar dateCal = getCalendar(date);
		Calendar timeCal = getCalendar(time);
		dateCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
		dateCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
		dateCal.set(Calendar.SECOND, timeCal.get(Calendar.SECOND));
		dateCal.set(Calendar.MILLISECOND, timeCal.get(Calendar.MILLISECOND));
		return dateCal.getTimeInMillis();
	}

	/**
	 * 将日期的时间部分清除后，转换成long类型
	 * 
	 * @param date
	 * @return
	 */
	public static long getDateNumber(Date date) {
		return removeTime(date).getTimeInMillis();
	}

	/**
	 * 获取日期(获取当天日期getDate(0))
	 * 
	 * @param day
	 * @return
	 */
	public static Date getDate(int day) {
		Calendar cal = getCalendar(new Date());
		cal.add(Calendar.DATE, day);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	/**
	 * 将时间的日期部分清除后，转换成long类型
	 * 
	 * @param date
	 * @return
	 */
	public static long getTimeNubmer(Date date) {
		return getCalendar(date).getTimeInMillis() - getDateNumber(date);
	}

	/**
	 * 将一个不包含日期的时间量，转换为Date类型，其中的日期为当天
	 * 
	 * @param l
	 * @return
	 */
	public static Date getTimeByNubmer(long l) {
		return new Date(getDateNumber(new Date()) + l);
	}

	public static Calendar getCalendar(long millis) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(millis);
		return cal;
	}

	public static Calendar getCalendar(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}

	public static Calendar removeTime(Date date) {
		if (date == null) {
			return null;
		}

		Calendar cal = getCalendar(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

	/**
	 * 在给定的时间点上增加小时，分钟
	 * 
	 * @param date
	 * @param hours
	 * @param minutes
	 * @return
	 */
	public static Date addTime(Date date, int hours, int minutes) {
		if (date == null) {
			return null;
		}
		Calendar cal = getCalendar(date);
		cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + hours);
		cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + minutes);
		return cal.getTime();
	}

	public static Date getNextDay(Date date, int day) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, day);
		return cal.getTime();
	}

	public static int transferTimeToSeconds(int hours, int minutes) {
		return (hours * 60 * 60) + (minutes * 60);
	}

	/**
	 * 日期加上天数的时间
	 * 
	 * @param date
	 * @return
	 */
	public static Date dateAddDay(Date date, int day) {
		return add(date, Calendar.DAY_OF_YEAR, day);
	}

	private static Date add(Date date, int type, int value) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(type, value);
		return calendar.getTime();
	}

	// 获得上周日日期
	public static String getWeekFirstDate() {
		SimpleDateFormat sf=new SimpleDateFormat(PATTERN_DATE);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.WEEK_OF_YEAR, -1);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		return sf.format(calendar.getTime());
	}

	// 获得上周六日期
	public static String getWeekLastDate() {
		SimpleDateFormat sf=new SimpleDateFormat(PATTERN_DATE);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.WEEK_OF_YEAR, -1);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		return sf.format(calendar.getTime());
	}
	
	//获取上个月的第一天
	public static String getMonthFirstDate(){
		SimpleDateFormat sf=new SimpleDateFormat(PATTERN_DATE);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return sf.format(calendar.getTime());
	}

	//获取上个月的最后一天
	public static String getMonthLastDate(){
		SimpleDateFormat sf=new SimpleDateFormat(PATTERN_DATE);
		Calendar calendar = Calendar.getInstance();
		int month = calendar.get(Calendar.MONTH);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return sf.format(calendar.getTime());
	}
	
	//获取上个季度的第一天
	public static String getQuarterFirstDate(){
		SimpleDateFormat sf=new SimpleDateFormat(PATTERN_DATE);
		Calendar calendar = Calendar.getInstance();  
		calendar.set(Calendar.MONTH, ((int) calendar.get(Calendar.MONTH) / 3 - 1) * 3);  
		calendar.set(Calendar.DAY_OF_MONTH, 1);  
		return sf.format(calendar.getTime());
	}
	
	//获取上个季度的最后一天
	public static String getQuarterLastDate(){
		SimpleDateFormat sf=new SimpleDateFormat(PATTERN_DATE);
		Calendar calendar = Calendar.getInstance();  
		calendar.set(Calendar.MONTH, ((int) calendar.get(Calendar.MONTH) / 3 - 1) * 3 + 2);  
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)); 
		return sf.format(calendar.getTime());
	}
	
	//获取去年的第一天
	public static String getYearFirstDate(){
		SimpleDateFormat sf=new SimpleDateFormat(PATTERN_DATE);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, -1);
		calendar.set(Calendar.DAY_OF_YEAR, 1);
		return sf.format(calendar.getTime());
	}
	
	//获取去年的最后一天
	public static String getYearLastDate(){
		SimpleDateFormat sf=new SimpleDateFormat(PATTERN_DATE);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, 0);
		calendar.set(Calendar.DAY_OF_YEAR, 0);
		return sf.format(calendar.getTime());
	}
	
	public static String getMonthmorning() {
		SimpleDateFormat sf=new SimpleDateFormat(PATTERN_DATE);
        Calendar calendar = Calendar.getInstance();  
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONDAY), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);  
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));  
        return  sf.format(calendar.getTime());
    }
	
}
