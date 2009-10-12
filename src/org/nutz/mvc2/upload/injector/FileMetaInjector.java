package org.nutz.mvc2.upload.injector;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc2.param.injector.NameInjector;
import org.nutz.mvc2.upload.TempFile;

public class FileMetaInjector extends NameInjector {

	public FileMetaInjector(String name) {
		super(name);
	}

	@SuppressWarnings("unchecked")
	public Object get(HttpServletRequest request, HttpServletResponse response, Object refer) {
		return ((TempFile) ((Map<String, Object>) refer).get(name)).getMeta();
	}

}
