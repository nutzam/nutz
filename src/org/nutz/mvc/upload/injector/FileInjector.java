package org.nutz.mvc.upload.injector;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.adaptor.ParamInjector;
import org.nutz.mvc.upload.TempFile;

public class FileInjector implements ParamInjector {
	
	private static final Log LOG = Logs.getLog(FileInjector.class);

	public FileInjector(String name) {
		this.name = name;
	}

	private String name;

	@SuppressWarnings("unchecked")
	public Object get(HttpServletRequest req, HttpServletResponse resp, Object refer) {
		Object obj = ((Map<String, Object>) refer).get(name);
		if (obj == null)
			return null;
		if (obj instanceof TempFile)
			return ((TempFile)obj).getFile();
		if (LOG.isWarnEnabled())
			LOG.warn("Form Field isn't a File!!");
		throw new RuntimeException("Form Field isn't a File!!");
	}
}
