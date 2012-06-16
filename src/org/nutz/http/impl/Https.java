package org.nutz.http.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Https {

	public static SimpleDateFormat httpDateFormat = new SimpleDateFormat("");
	public static Date httpData(String str) throws ParseException {
		return httpDateFormat.parse(str);
	}
	public static String httpDate(Date date) {
		return httpDateFormat.format(date);
	}
}
