package org.nutz.mvc.adaptor;

import java.util.HashMap;
import java.util.Map;

import org.nutz.castor.castor.Map2Object;
import org.nutz.lang.Strings;


public class Args {
	private Map<String,String> params = new HashMap<String, String>();
	
	public String put(String key,String value){
		String oldValue = params.get(key);
		if(Strings.isEmpty(oldValue)){
			params.put(key, value);
		}else{
			params.put(key, oldValue+","+value);
		}
		return params.get(key);
	}
	public String get(String key){
		return params.get(key);
	}
	public <T> T toFill(Class<T> clazz) throws InstantiationException, IllegalAccessException{
		@SuppressWarnings("unchecked")
		T obj = (T)(new Map2Object().cast(params,clazz));
		return obj;
	}
}
