package org.nutz.mvc.upload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.filepool.NutFilePool;
import org.nutz.lang.Lang;
import org.nutz.mvc.adaptor.AbstractAdaptor;
import org.nutz.mvc.adaptor.ParamInjector;
import org.nutz.mvc.adaptor.injector.MapPairInjector;
import org.nutz.mvc.adaptor.injector.ObjectPairInjector;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.upload.injector.FileInjector;
import org.nutz.mvc.upload.injector.FileMetaInjector;
import org.nutz.mvc.upload.injector.InputStreamInjector;
import org.nutz.mvc.upload.injector.MapArrayInjector;
import org.nutz.mvc.upload.injector.MapItemInjector;
import org.nutz.mvc.upload.injector.MapListInjector;
import org.nutz.mvc.upload.injector.MapSelfInjector;
import org.nutz.mvc.upload.injector.ReaderInjector;
import org.nutz.mvc.upload.injector.TempFileInjector;

/**
 * 本适配器专门处理 HTTP 文件上传。 它支持多文件，多参数上传。具体的做法是将 HTTP 上传的所有内容
 * 包括文件以及名值对都预先缓存下来。其中，文件缓存在磁盘上，名值对缓存在内存中。
 * <p>
 * 因此，本适配器构造的时候，需要四个参数：
 * <ol>
 * <li>临时文件存放的目录
 * <li>数据缓冲区大小,建议设置为8192
 * <li>HTTP 请求的编码方式。
 * <li>临时文件的最大数量
 * </ol>
 * 本适配器提供了四个构造函数，最简单的一个只有一个参数，需要你提供一个临时文件目录,缓冲区大小默认为8192, 临时文件数目默认的为 "2000"，HTTP
 * 请求的编码方式为 "UTF-8",
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

	private UploadingContext context;

	public UploadAdaptor() throws IOException {
		context = new UploadingContext(new NutFilePool(File.createTempFile("nutz", null)
															.getParent(), 2000));
	}

	public UploadAdaptor(UploadingContext context) {
		this.context = context;
	}

	public UploadAdaptor(String path) {
		context = new UploadingContext(path);
	}

	public UploadAdaptor(String path, int buffer) {
		this(path);
		context.setBufferSize(buffer);
	}

	public UploadAdaptor(String path, int buffer, String charset) {
		this(path);
		context.setBufferSize(buffer);
		context.setCharset(charset);
	}

	public UploadAdaptor(String path, int buffer, String charset, int poolSize) {
		context = new UploadingContext(new NutFilePool(path, poolSize));
		context.setBufferSize(buffer);
		context.setCharset(charset);
	}

	public UploadAdaptor(String path, int buffer, String charset, int poolSize, int maxFileSize) {
		context = new UploadingContext(new NutFilePool(path, poolSize));
		context.setBufferSize(buffer);
		context.setCharset(charset);
		context.setMaxFileSize(maxFileSize);
	}

	public UploadingContext getContext() {
		return context;
	}

	protected ParamInjector evalInjector(Class<?> type, Param param) {
		// Map
		if (Map.class.isAssignableFrom(type))
			return new MapSelfInjector();

		if (null == param)
			return null;

		String paramName = param.value();

		// File
		if (File.class.isAssignableFrom(type))
			return new FileInjector(paramName);
		// FileMeta
		if (FieldMeta.class.isAssignableFrom(type))
			return new FileMetaInjector(paramName);
		// TempFile
		if (TempFile.class.isAssignableFrom(type))
			return new TempFileInjector(paramName);
		// InputStream
		if (InputStream.class.isAssignableFrom(type))
			return new InputStreamInjector(paramName);
		// Reader
		if (Reader.class.isAssignableFrom(type))
			return new ReaderInjector(paramName);
		// List
		if (List.class.isAssignableFrom(type))
			return new MapListInjector(paramName);
		// Array
		if (type.isArray())
			return new MapArrayInjector(type.getComponentType(), paramName);
		// POJO
		if ("..".equals(paramName)) {
			if (type.isAssignableFrom(Map.class))
				return new MapPairInjector();
			return new ObjectPairInjector(null, type);
			// return new MapReferInjector(null, type);
		}
		// POJO with prefix
		else if (paramName.startsWith("::") && paramName.length() > 2) {
			return new ObjectPairInjector(null, type);
			// return new MapReferInjector(paramName.substring(2), type);
		}

		// Default case
		return new MapItemInjector(paramName, type);
	}

	public Object[] adapt(	ServletContext sc,
							HttpServletRequest request,
							HttpServletResponse response,
							String[] pathArgs) {
		Map<String, Object> map;
		try {
			Uploading ing = new FastUploading();
			map = ing.parse(request, context);
		}
		catch (UploadException e) {
			throw Lang.wrapThrow(e);
		}
		finally {
			Uploads.removeInfo(request);
		}
		// Try to make the args
		Object[] args = new Object[injs.length];
		int i = fillPathArgs(request, response, pathArgs, args);
		// Inject another params
		for (; i < injs.length; i++) {
			args[i] = injs[i].get(sc, request, response, map);
		}
		return args;
	}
}
