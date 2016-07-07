package com.ailk.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.ailk.core.exception.AppRuntimeException;

public class DateUtil {

	private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
	
	public static String format(Date date){
		SimpleDateFormat format = new SimpleDateFormat(DEFAULT_PATTERN);
		return format.format(date);
	}
	
	public static String format(Date date, String pattern){
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(date);
	}
	
	public static Date parse(String str, String pattern){
		Date date = null;
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			throw new RuntimeException("parse date exception.", e);
		}
		return date;
	}
	
	public static List<String> getIntervDate(String startDate, String endDate, String pattern) throws AppRuntimeException{
		List<String> dayList = new ArrayList<String>();
		try{
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		Date start = format.parse(startDate);
		Date end = format.parse(endDate);
		Calendar c = Calendar.getInstance();
		c.setTime(start);
		
		dayList.add(startDate);
		while(c.getTime().compareTo(end) < 0){
			c.add(Calendar.DATE, 1);
			dayList.add(format.format(c.getTime()));
		}
		}catch(Exception e){
			throw new AppRuntimeException("parse date[startDate: "+ startDate +", endDate: "+ endDate +", pattern: "+ pattern +"] excepiton", e);
		}
		return dayList;
	}
	
	public static List<String> getIntervMonth(String startDate, String endDate, String pattern) throws AppRuntimeException{
		List<String> dayList = new ArrayList<String>();
		try{
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		Date start = format.parse(startDate);
		Date end = format.parse(endDate.substring(0, 6) + "01");
		Calendar c = Calendar.getInstance();
		c.setTime(start);
		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), 1);
		format.applyPattern("yyyyMM");
		dayList.add(startDate.substring(0, 6));
		while(c.getTime().compareTo(end) < 0){
			c.add(Calendar.MONTH, 1);
			dayList.add(format.format(c.getTime()));
		}
		}catch(Exception e){
			throw new AppRuntimeException("parse date[startDate: "+ startDate +", endDate: "+ endDate +", pattern: "+ pattern +"] excepiton", e);
		}
		//System.out.println(dayList.size());
		return dayList;
	}
	
	public static Date addMonth(Date date, int n){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MONTH, n);
		return c.getTime();
	}
	
	public static void main(String[] args){
		getIntervMonth("20121201", "20130130","yyyyMMdd");
	}
	
	
}
