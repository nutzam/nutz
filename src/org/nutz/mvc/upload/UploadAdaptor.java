package org.nutz.mvc.upload;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.filepool.FilePool;
import org.nutz.filepool.NutFilePool;
import org.nutz.lang.Encoding;
import org.nutz.lang.Lang;
import org.nutz.mvc.adaptor.AbstractAdaptor;
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
 * 本适配器专门处理 HTTP 文件上传。 它支持多文件，多参数上传。具体的做法是将 HTTP 上传的所有内容
 * 包括文件以及名值对都预先缓存下来。其中，文件缓存在磁盘上，名值对缓存在内存中。
 * <p>
 * 因此，本适配器构造的时候，需要三个参数：
 * <ol>
 * <li>临时文件存放的目录
 * <li>临时文件的总数目，超过了这个数目，旧的临时文件就有可能被删除
 * <li>HTTP 请求的编码方式。
 * </ol>
 * 本适配器提供了三个构造函数，最简单的一个只有一个参数，需要你提供一个临时文件目录 临时文件数目默认的为 "2000"，HTTP 请求的编码方式为
 * "UTF-8"
 * <p>
 * 为了能让入口函数了解 HTTP 请求的更多信息，本适配器入口函数声明更多的参数类型：
 * <ul>
 * <li>java.io.File : 指向已上传至临时目录的文件对象
 * <li>org.nutz.mvc.upload.FieldMeta : 描述了一个上传参数的更多属性
 * <li>org.nutz.mvc.upload.TempFile : 组合了 File 和 FieldMeta
 * </ul>
 * 当然，这三种参数，都是需要你在入口函数的参数列表里声明 '@Param' 注解，用来告诉本适配器，你的参数 具体取自请求中的哪一个参数。
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 * 
 * @see org.nutz.mvc.annotation.Param
 */
public class UploadAdaptor extends AbstractAdaptor {

	private String charset;
	private FilePool pool;
	private int buffer;

	public UploadAdaptor() throws IOException {
		this(File.createTempFile("nutz", null).getParent());
	}

	public UploadAdaptor(String path) {
		this(path, 8192, Encoding.UTF8, 2000);
	}

	public UploadAdaptor(String path, int buffer) {
		this(path, buffer, Encoding.UTF8, 2000);
	}

	public UploadAdaptor(String path, int buffer, String charset) {
		this(path, buffer, charset, 2000);
	}

	public UploadAdaptor(String path, int buffer, String charset, int poolSize) {
		this.charset = charset;
		this.pool = new NutFilePool(path, poolSize);
		this.buffer = buffer;
	}

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

	public Object[] adapt(	HttpServletRequest request,
							HttpServletResponse response,
							String[] pathArgs) {
		Map<String, Object> map;
		try {
			Uploading ing = new FastUploading(buffer);
			map = ing.parse(request, charset, pool);
		}
		catch (UploadException e) {
			throw Lang.wrapThrow(e);
		}
		// Try to make the args
		Object[] args = new Object[injs.length];
		int i = fillPathArgs(request, response, pathArgs, args);
		// Inject another params
		for (; i < injs.length; i++) {
			args[i] = injs[i].get(request, response, map);
		}
		return args;
	}
}
