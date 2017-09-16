package org.nutz.mvc.adaptor;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.filepool.FilePool;
import org.nutz.filepool.UU32FilePool;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.upload.FastUploading;
import org.nutz.mvc.upload.FieldMeta;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadException;
import org.nutz.mvc.upload.UploadingContext;
import org.nutz.mvc.upload.injector.*;

/**
 * 自动适配普通表单/文件上传表单/Json表单的数据
 * 
 */
public class WhaleAdaptor extends PairAdaptor {
	
	protected static Log log = Logs.get();
	
	protected UploadingContext uploadCtx;
	
	public WhaleAdaptor() {
		this("");
	}

	public WhaleAdaptor(String path) {
        String appRoot = Mvcs.getServletContext().getRealPath("/");
        if (appRoot == null) {
            appRoot = (String) Mvcs.getServletContext().getAttribute("javax.servlet.context.tmpdir");
            if (appRoot == null) {
                appRoot = System.getProperty("java.io.tmpdir");
                if (appRoot == null)
                    appRoot = "/tmp";
            }
        }
        if (path.isEmpty()) {
            path = "${app.root}/WEB-INF/tmp/nutzupload2";
        }
        if (path.contains("${app.root}"))
            path = path.replace("${app.root}", appRoot);
        uploadCtx = new UploadingContext(new UU32FilePool(path));
    }

    public WhaleAdaptor(FilePool pool) {
        this(new UploadingContext(pool));
    }

    public WhaleAdaptor(UploadingContext up) {
        uploadCtx = up;
    }
	
	@SuppressWarnings("deprecation")
	protected ParamInjector evalInjectorBy(Type type, Param param) {
        // TODO 这里的实现感觉很丑, 感觉可以直接用type进行验证与传递
        // TODO 这里将Type的影响局限在了 github issue #30 中提到的局部范围
        Class<?> clazz = Lang.getTypeClass(type);
        if (clazz == null) {
            if (log.isWarnEnabled())
                log.warnf("!!Fail to get Type Class : type=%s , param=%s", type, param);
            return null;
        }

        // Map
        if (Map.class.isAssignableFrom(clazz))
            return new MapSelfInjector();

        String pn = null == param ? paramNames[curIndex] : param.value();

        // File
        if (File.class.isAssignableFrom(clazz))
            return new FileInjector(pn);
        // FileMeta
        if (FieldMeta.class.isAssignableFrom(clazz))
            return new FileMetaInjector(pn);
        // TempFile
        if (TempFile.class.isAssignableFrom(clazz))
            return new TempFileInjector(pn);
        // InputStream
        if (InputStream.class.isAssignableFrom(clazz))
            return new InputStreamInjector(pn);
        // Reader
        if (Reader.class.isAssignableFrom(clazz))
            return new ReaderInjector(pn);
        // List
        //if (List.class.isAssignableFrom(clazz)) {
        //    return new MapListInjector(paramName);
        //}
        if (TempFile[].class.isAssignableFrom(clazz)) {
            return new TempFileArrayInjector(pn);
        }
        // Other
        return super.evalInjectorBy(type, param);
    }

	protected Object getReferObject(ServletContext sc, HttpServletRequest req, HttpServletResponse resp, String[] pathArgs) {
		String type = req.getHeader("Content-Type");
		if (!Strings.isBlank(type)) {
			if (type.contains("json")) { // JSON适配器
				try {
					return Json.fromJson(req.getReader());
				} catch (Exception e) {
					throw Lang.wrapThrow(e);
				}
			}
			if (type.contains("multipart/form-data")) { // 上传适配器
				FastUploading uploading = new FastUploading();
				try {
					return uploading.parse(req, uploadCtx);
				} catch (UploadException e) {
					throw Lang.wrapThrow(e);
				}
			}
		}
		return super.getReferObject(sc, req, resp, pathArgs);
	}
	
}
