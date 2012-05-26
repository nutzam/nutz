package org.nutz.template.util;

import java.util.HashMap;
import java.util.Map;

import org.nutz.mvc.Mvcs;

public class LocalMessage {
	
	private static Map<String,String> messages  = new HashMap<String, String>();
	private static boolean inited = false;
	public static String get(String name){
		if(!inited){
			synchronized (messages) {
				if(!inited){
					messages = Mvcs.getDefaultLocaleMessage(Mvcs.getServletContext());
				}
			}
		}
		String value = messages.get(name);
		if(value ==null) return name;
		else return value;
	}
}
