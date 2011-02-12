package org.nutz.mvc2.impl;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ActionFilters {

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

}
