package org.nutz.log.helper;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;

public class TestClassLoader extends ClassLoader {

	private ClassLoader realLoader = null;
	
	private boolean canWeFoundLog4jDefaultProperties = true;
	
	public TestClassLoader(ClassLoader realClassLoader) {
		this.realLoader = realClassLoader;
	}

	public void clearAssertionStatus() {
		realLoader.clearAssertionStatus();
	}

	public boolean equals(Object obj) {
		return realLoader.equals(obj);
	}

	public URL getResource(String name) {
		
		if ("log4j.xml".equals(name) || "log4j.properties".equals(name))
			try {
				return canWeFoundLog4jDefaultProperties?new URL("file:///c:/" + name):null;
			} catch (MalformedURLException e) {
				e.printStackTrace(System.err);
			} 
			
		return realLoader.getResource(name);
	}

	public InputStream getResourceAsStream(String name) {
		return realLoader.getResourceAsStream(name);
	}

	public Enumeration<URL> getResources(String name) throws IOException {
		return realLoader.getResources(name);
	}

	public int hashCode() {
		return realLoader.hashCode();
	}

	public Class<?> loadClass(String name) throws ClassNotFoundException {
		return realLoader.loadClass(name);
	}

	public void setClassAssertionStatus(String className, boolean enabled) {
		realLoader.setClassAssertionStatus(className, enabled);
	}

	public void setDefaultAssertionStatus(boolean enabled) {
		realLoader.setDefaultAssertionStatus(enabled);
	}

	public void setPackageAssertionStatus(String packageName, boolean enabled) {
		realLoader.setPackageAssertionStatus(packageName, enabled);
	}

	public String toString() {
		return realLoader.toString();
	}

	public boolean isCanWeFoundLog4jDefaultProperties() {
		return canWeFoundLog4jDefaultProperties;
	}

	public void setCanWeFoundLog4jDefaultProperties(boolean canWeFoundLog4jDefaultProperties) {
		this.canWeFoundLog4jDefaultProperties = canWeFoundLog4jDefaultProperties;
	}
	
	
}
