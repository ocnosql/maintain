package com.ailk.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseUtil {

	public static List<String> parseSql(String sql){
		Pattern pattern = Pattern.compile("(\\$(\\{.*?\\}))");
		Matcher matcher = pattern.matcher(sql);
		List<String> v = new ArrayList<String>();
		while(matcher.find()){
			v.add(matcher.group(1));
			//System.out.println(matcher.group(1));
		}
		return v;
	}
	
	public static void main(String[] args){
		String sql = "select * from ${table} where query_date between ${startDate} and ${endDate} and bill_id=${billId}";
		parseSql(sql);
	}
}
