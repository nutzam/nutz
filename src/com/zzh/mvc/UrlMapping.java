package com.zzh.mvc;

public class UrlMapping {

	private String url;

	private String moduleName;

	private String viewName;

	private String controllorName;

	private String serviceName;

	public UrlMapping() {
	}

	public UrlMapping(String url, String moduleName, String viewName, String controllorName,
			String serviceName) {
		this.url = url;
		this.moduleName = moduleName;
		this.viewName = viewName;
		this.controllorName = controllorName;
		this.serviceName = serviceName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public String getControllorName() {
		return controllorName;
	}

	public void setControllorName(String controllorName) {
		this.controllorName = controllorName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

}
