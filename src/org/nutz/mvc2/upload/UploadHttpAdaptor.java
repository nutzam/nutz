package org.nutz.mvc2.upload;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.castor.Castors;
import org.nutz.filepool.FilePool;
import org.nutz.filepool.NutFilePool;
import org.nutz.lang.Lang;
import org.nutz.mvc2.param.AbstractHttpAdaptor;
import org.nutz.mvc2.param.ParamBean;

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

	public Object[] adapt(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> map;
		try {
			map = new Uploading().parse(request, charset, pool).params;
		} catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
		Object[] args = new Object[params.length];
		for (int i = 0; i < params.length; i++) {
			ParamBean p = params[i];
			if(isNeedSkip(request, response, args, i, p))
				continue;
			Object value = map.get(p.getName());
			if (value instanceof TempFile) {
				TempFile tf = (TempFile) value;
				if (p.getType().isAssignableFrom(File.class)) {
					args[i] = tf.getFile();
				} else if (p.getType().isAssignableFrom(FieldMeta.class)) {
					args[i] = tf.getMeta();
				} else if (p.getType().isAssignableFrom(TempFile.class)) {
					args[i] = tf;
				} else {
					throw Lang.makeThrow("Unexpect type '%s' for binary form field [%d]'%s'", p.getType().getName(), i,
							p.getName());
				}
			} else {
				args[i] = Castors.me().castTo(value, p.getType());
			}
		}
		return args;
	}
}
