package org.nutz.mvc.upload;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.filepool.FilePool;
import org.nutz.filepool.NutFilePool;
import org.nutz.lang.Lang;
import org.nutz.mvc.param.AbstractHttpAdaptor;
import org.nutz.mvc.param.ParamInjector;
import org.nutz.mvc.upload.injector.FileInjector;
import org.nutz.mvc.upload.injector.FileMetaInjector;
import org.nutz.mvc.upload.injector.TempFileInjector;

public class UploadHttpAdaptor extends AbstractHttpAdaptor {

	private String charset;
	private FilePool pool;

	public UploadHttpAdaptor(String path) {
		this(path, "2000");
	}

	public UploadHttpAdaptor(String path, String size) {
		this(path, size, "UTF-8");
	}

	public UploadHttpAdaptor(String path, String size, String charset) {
		this.charset = charset;
		this.pool = new NutFilePool(path, Integer.parseInt(size));
	}

	protected ParamInjector evalInjector(Class<?> type, String name) {
		// File
		if (type.isAssignableFrom(File.class))
			return new FileInjector(name);
		// FileMeta
		if (type.isAssignableFrom(FieldMeta.class))
			return new FileMetaInjector(name);
		// TempFile
		if (type.isAssignableFrom(TempFile.class))
			return new TempFileInjector(name);

		return null;
	}

	public Object[] adapt(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> map;
		try {
			map = new Uploading().parse(request, charset, pool).params;
		} catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
		// Try to make the args
		Object[] args = new Object[injs.length];
		for (int i = 0; i < injs.length; i++) {
			args[i] = injs[i].get(request, response, map);
		}
		return args;
	}
}
