/**
 * 
 */
package cc.joymaker.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author zhe
 *
 */
public class DateUtils {
	static Logger log = LoggerFactory.getLogger(DateUtils.class);

	private static String defaultDatePattern = "yyyy-MM-dd HH:mm:ss";
	private static String defaultDateShortPattern = "yyyy-MM-dd";
	public static String defaultDatePatternShort = "yyyy年MM月dd日 HH:mm";
	public static String yyyyMMdd = "yyyyMMdd";
	public static String hhmmssSSS = "HHmmssSSS";
	public static String HHmm = "HH:mm";
	public static String yyyyMM = "yyyyMM";

	public static Date parse(String strDate) {

		if (StringUtils.isBlank(strDate)) {
			return null;
		}

		SimpleDateFormat sdf = new SimpleDateFormat(defaultDatePattern);
		Date date = null;
		try {
			date = sdf.parse(strDate);
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
		}

		return date;
	}

	public static Date parseShort(String strDate) {

		if (StringUtils.isBlank(strDate)) {
			return null;
		}

		SimpleDateFormat sdf = new SimpleDateFormat(defaultDateShortPattern);
		Date date = null;
		try {
			date = sdf.parse(strDate);
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
		}

		return date;
	}

	public static String parse(Date date) {
		if (date == null) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(defaultDatePattern);
		return sdf.format(date);
	}

	public static String formatDate(Date date, String pattern) {
		if (date == null) {
			return "";
		}
		if (StringUtils.isBlank(pattern)) {
			pattern = yyyyMMdd;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}

	/**
	 * 日期间隔的分钟
	 * 
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	public static Long getBetweenMinutes(Date beginTime, Date endTime) {
		if (endTime == null || beginTime == null) {
			return 0L;
		}
		return (endTime.getTime() - beginTime.getTime()) / (60 * 1000);
	}

	public static Date getSundayOfThisWeek() {
		Calendar c = Calendar.getInstance();
		int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (day_of_week == 0)
			day_of_week = 7;
		c.add(Calendar.DATE, -day_of_week + 7);
		return c.getTime();
	}

	public static void main(String[] args) {
	}

	public static String getUTCTimeStr() {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

		// 1、取得本地时间：
		Calendar cal = Calendar.getInstance();
		// 2、取得时间偏移量：
		int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
		// 3、取得夏令时差：
		int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
		// 4、从本地时间里扣除这些差量，即可以取得UTC时间：
		cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
		return format.format(cal.getTime());
	}
}
