package org.nutz.mvc.upload;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.nutz.mvc.adaptor.ParamInjector;
import org.nutz.mvc.adaptor.injector.ArrayInjector;
import org.nutz.mvc.adaptor.injector.MapPairInjector;
import org.nutz.mvc.adaptor.injector.MapReferInjector;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.upload.injector.FileInjector;
import org.nutz.mvc.upload.injector.FileMetaInjector;
import org.nutz.mvc.upload.injector.MapItemInjector;
import org.nutz.mvc.upload.injector.MapSelfInjector;
import org.nutz.mvc.upload.injector.TempFileInjector;

/**
 * PairAdaptor和UploadAdaptor的结合
 * 用于在上传文件的同事，也能支持高级的POJO注入方式
 * 
 * @author lAndRaxeE(landraxee@gmail.com)
 *
 */
public class PairUploadAdaptor extends UploadAdaptor {

	public PairUploadAdaptor() throws IOException {
		super();
	}

	public PairUploadAdaptor(String path, int buffer, String charset,
			int poolSize) {
		super(path, buffer, charset, poolSize);
	}

	public PairUploadAdaptor(String path, int buffer, String charset) {
		super(path, buffer, charset);
	}

	public PairUploadAdaptor(String path, int buffer) {
		super(path, buffer);
	}

	public PairUploadAdaptor(String path) {
		super(path);
	}

	@Override
	protected ParamInjector evalInjector(Class<?> type, Param param) {
		// File
		if (File.class.isAssignableFrom(type))
			return new FileInjector(param.value());
		// FileMeta
		if (FieldMeta.class.isAssignableFrom(type))
			return new FileMetaInjector(param.value());
		// TempFile
		if (TempFile.class.isAssignableFrom(type))
			return new TempFileInjector(param.value());
		// Map
		if (Map.class.isAssignableFrom(type))
			return new MapSelfInjector();
		
		if (null == param)
			return null;

		String pm = param.value();
		// POJO
		if ("..".equals(pm)) {
			if (type.isAssignableFrom(Map.class))
				return new MapPairInjector();
			return new MapReferInjector(null, type);
		}
		// POJO with prefix
		else if (pm.startsWith("::") && pm.length() > 2) {
			return new MapReferInjector(pm.substring(2), type);
		}
		// POJO[]
		else if (type.isArray())
			return new ArrayInjector(pm, type);

		return new MapItemInjector(param.value(), type);
	}

}
