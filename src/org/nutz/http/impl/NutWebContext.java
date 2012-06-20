package org.nutz.http.impl;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.nutz.lang.Lang;

public class NutWebContext extends HttpObject {
	
	//-----------------------------------------------------------
	protected Map<String,String> initParams = new HashMap<String, String>();
	
	public String getInitParameter(String name) {
		return initParams.get(name);
	}
	
	public Enumeration<String> getInitParameterNames() {
		return Collections.enumeration(initParams.keySet());
	}
	//----------------------------------------------------------
	
	public String getMimeType(String name) {
		throw Lang.noImplement();
	}
	//----------------------------------------------------------
	
	public String getServerInfo() {
		return "Nutz Serlvet Container";
	}
}
