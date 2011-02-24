package org.nutz.mock.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;

import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class MockServletContext extends MockServletObject implements ServletContext {
	
	private static final Log log = Logs.getLog(MockServletContext.class);

	public int getMajorVersion() {
		throw Lang.noImplement();
	}

	public String getMimeType(String arg0) {
		throw Lang.noImplement();
	}

	public int getMinorVersion() {
		throw Lang.noImplement();
	}

	public RequestDispatcher getNamedDispatcher(String arg0) {
		throw Lang.noImplement();
	}

	public String getRealPath(String path) {
		if (path.startsWith("/WEB-INF/lib/"))
			return new File(path.substring("/WEB-INF/lib/".length())).getAbsolutePath();
		if (path.startsWith("/WEB-INF/classes/"))
			return new File(path.substring("/WEB-INF/classes/".length())).getAbsolutePath();
		if (path.startsWith("/"))
			return new File("."+path).getAbsolutePath();
		return new File(path).getAbsolutePath();
	}

	public RequestDispatcher getRequestDispatcher(String arg0) {
		throw Lang.noImplement();
	}

	public URL getResource(String name) throws MalformedURLException {
		return getClass().getResource(name);
	}

	public InputStream getResourceAsStream(String name) {
		return getClass().getResourceAsStream(name);
	}

	public Set<String> getResourcePaths(String name) {
		try {
			HashSet<String> hashSet = new HashSet<String>();
			Enumeration<URL> enumeration;
			enumeration = getClass().getClassLoader().getResources(name);
			while (enumeration.hasMoreElements()) {
				URL url = (URL) enumeration.nextElement();
				hashSet.add(url.toString());
			}
			return hashSet;
		}
		catch (IOException e) {
			log.info("IOException", e);
			return null;
		}
	}

	public String getServerInfo() {
		throw Lang.noImplement();
	}

	public Servlet getServlet(String name) throws ServletException {
		throw Lang.noImplement();
	}

	private String servletContextName;

	public String getServletContextName() {
		return servletContextName;
	}

	public void setServletContextName(String servletContextName) {
		this.servletContextName = servletContextName;
	}

	public Enumeration<String> getServletNames() {
		throw Lang.noImplement();
	}

	public Enumeration<Servlet> getServlets() {
		throw Lang.noImplement();
	}

	public void log(String arg0) {
		log.info(arg0);
	}

	public void log(Exception arg0, String arg1) {
		log.info(arg1, arg0);
	}

	public void log(String arg0, Throwable arg1) {
		log.info(arg0, arg1);
	}

	protected Map<String, Object> attributeMap = new HashMap<String, Object>();

	public void removeAttribute(String key) {
		attributeMap.remove(key);
	}

	public void setAttribute(String key, Object value) {
		attributeMap.put(key, value);
	}

	public Object getAttribute(String key) {
		return attributeMap.get(key);
	}

	public Enumeration<String> getAttributeNames() {
		return new Vector<String>(attributeMap.keySet()).elements();
	}

	public ServletContext getContext(String arg0) {
		throw Lang.noImplement();
	}

	public String getContextPath() {
		throw Lang.noImplement();
	}

	
	public Dynamic addFilter(String arg0, String arg1) {
		throw Lang.noImplement();
	}

	
	public Dynamic addFilter(String arg0, Filter arg1) {
		throw Lang.noImplement();
	}

	
	public Dynamic addFilter(String arg0, Class<? extends Filter> arg1) {
		throw Lang.noImplement();
	}

	
	public void addListener(Class<? extends EventListener> arg0) {
		throw Lang.noImplement();
	}

	
	public void addListener(String arg0) {
		throw Lang.noImplement();
	}

	
	public <T extends EventListener> void addListener(T arg0) {
		throw Lang.noImplement();
	}

	
	public javax.servlet.ServletRegistration.Dynamic addServlet(String arg0,
			String arg1) {
		throw Lang.noImplement();
	}

	
	public javax.servlet.ServletRegistration.Dynamic addServlet(String arg0,
			Servlet arg1) {
		throw Lang.noImplement();
	}

	
	public javax.servlet.ServletRegistration.Dynamic addServlet(String arg0,
			Class<? extends Servlet> arg1) {
		throw Lang.noImplement();
	}

	
	public <T extends Filter> T createFilter(Class<T> arg0)
			throws ServletException {
		throw Lang.noImplement();
	}

	
	public <T extends EventListener> T createListener(Class<T> arg0)
			throws ServletException {
		throw Lang.noImplement();
	}

	
	public <T extends Servlet> T createServlet(Class<T> arg0)
			throws ServletException {
		throw Lang.noImplement();
	}

	
	public void declareRoles(String... arg0) {
		throw Lang.noImplement();
	}

	
	public ClassLoader getClassLoader() {
		throw Lang.noImplement();
	}

	
	public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
		throw Lang.noImplement();
	}

	
	public int getEffectiveMajorVersion() {
		throw Lang.noImplement();
	}

	
	public int getEffectiveMinorVersion() {
		throw Lang.noImplement();
	}

	
	public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
		throw Lang.noImplement();
	}

	
	public FilterRegistration getFilterRegistration(String arg0) {
		throw Lang.noImplement();
	}

	
	public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
		throw Lang.noImplement();
	}

	
	public JspConfigDescriptor getJspConfigDescriptor() {
		throw Lang.noImplement();
	}

	
	public ServletRegistration getServletRegistration(String arg0) {
		throw Lang.noImplement();
	}

	
	public Map<String, ? extends ServletRegistration> getServletRegistrations() {
		throw Lang.noImplement();
	}

	
	public SessionCookieConfig getSessionCookieConfig() {
		throw Lang.noImplement();
	}

	
	public boolean setInitParameter(String arg0, String arg1) {
		throw Lang.noImplement();
	}

	
	public void setSessionTrackingModes(Set<SessionTrackingMode> arg0)
			throws IllegalStateException, IllegalArgumentException {
		throw Lang.noImplement();
	}

}
