package org.nutz.mvc.util;

import java.util.HashMap;
import java.util.Map;

import org.nutz.mvc.Mvcs;

public class LocalMessage {
	
	private static Map<String, Object> messages  = new HashMap<String, Object>();
	private static boolean inited = false;
	public static Object get(String name){
		if(!inited){
			synchronized (messages) {
				if(!inited){
					messages = Mvcs.getLocaleMessage(Mvcs.DEFAULT_MSGS);
				}
			}
		}
		Object value = messages.get(name);
		if(value ==null) return name;
		else return value;
	}
}
