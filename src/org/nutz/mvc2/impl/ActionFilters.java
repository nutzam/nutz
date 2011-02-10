package org.nutz.mvc2.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc2.ActionFilter;

public class ActionFilters {
	
	static List<ActionFilter> list = new ArrayList<ActionFilter>();
	
	static {
		list.add(new UpdateRequestAttributesFilter());
		list.add(new UriMappingActionFilter());
		list.add(new EncodingFilter());
		list.add(new ViewFilter());
		list.add(new OldActionFilter());
		list.add(new HttpAdaptorFilter());
		list.add(new ActionInvokeObjectFilter());
		list.add(new MethodInvokeActionFilter());
	}

	public static final String path = "path";
	public static final String pathArgs = "pathArgs";
	
	public static final String moduleObject = "module";
	public static final String moduleType = "moduleType";
	public static final String moduleName = "moduleName";
	public static final String methodObj = "method.obj";
	
	public static final String requestEncoding = "request.encoding";
	public static final String responseEncoding = "response.encoding";
	
	public static final String request = HttpServletRequest.class.getName();
	public static final String response = HttpServletResponse.class.getName();
	public static final String servletContent = ServletContext.class.getName();
	
	public static final String method = "method";
	public static final String methodObject = "method.obj";
	public static final String methodArgs = "method.args";
	public static final String returnValue = "obj";
	
	public static final String adaptor = "adaptor";
	
	public static final String viewOK = "view.ok";
	public static final String viewFail = "view.fail";
	
	public static final String oldActionFilters = "old.filters";
	
	public static final List<ActionFilter> defaultFilters(){
		return list;
	}
}
