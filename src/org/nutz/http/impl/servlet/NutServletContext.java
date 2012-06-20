package org.nutz.http.impl.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public abstract class NutServletContext implements ServletContext {

	//----------------------------------------------------------
	private static final Log log = Logs.get();
	
	public void log(String msg) {
		log.debug(msg);
	}
	public void log(String msg, Throwable e) {
		log.debug(msg, e);
	}

	//------------------------------------------------------------
	
	public int getMajorVersion() {
		return 2;
	}
	
	public int getMinorVersion() {
		return 5;
	}

	//----------------------------------------------------------
	protected String contextPath;
	
	public String getContextPath() {
		return contextPath;
	}
	
	public String getServletContextName() {
		return "nutz";
	}
	//----------------------------------------------------------
	//Resource
	
	public URL getResource(String path) throws MalformedURLException {
		if (path == null)
			throw new NullPointerException("path");
		if (!path.startsWith("/"))
			throw new IllegalArgumentException("Must start with /");
		File f = new File(root + path);
		if (!f.exists())
			return null;
		return f.toURI().toURL();
	}
	
	public InputStream getResourceAsStream(String path) {
		try {
			URL url = getResource(path);
			if (url == null)
				return null;
			return url.openStream();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public Set<String> getResourcePaths(String path) {
		if (path == null)
			path = "/";
		else if (!path.startsWith("/"))
			path = "/" + path;
		File f = new File(root + path);
		Set<String> ns = new HashSet<String>();
		if (f.isDirectory()) {
			ns.add(path);
		} else {
			if (!path.endsWith("/"))
				path += "/";
			String[] names = f.list();
			if (names == null)
				return ns;
			for (String name : names) {
				name = path + name;
				if (new File(name).isDirectory())
					name += "/";
				ns.add(name);
			}
		}
		return ns;
	}
	protected String root;
	
	public String getRealPath(String path) {
		return root + path;
	}
	//----------------------------------------------------------
	
	public RequestDispatcher getRequestDispatcher(String path) {
		throw Lang.noImplement();
	}
	//----------------------------------------------------------
	//这以下的,是不打算实现的API
	
	public ServletContext getContext(String paramString) {
		throw Lang.noImplement();
	}
	
	public RequestDispatcher getNamedDispatcher(String paramString) {
		throw Lang.noImplement();
	}
	public Servlet getServlet(String paramString) throws ServletException {
		throw Lang.noImplement();
	}
	
	public Enumeration<Servlet> getServlets() {
		throw Lang.noImplement();
	}
	
	public void log(Exception paramException, String paramString) {
		throw Lang.noImplement();
	}

	public Enumeration<String> getServletNames() {
		throw Lang.noImplement();
	}
}
