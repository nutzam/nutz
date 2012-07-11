package org.nutz.mvc.upload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.filepool.NutFilePool;
import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.adaptor.PairAdaptor;
import org.nutz.mvc.adaptor.ParamInjector;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.upload.injector.FileInjector;
import org.nutz.mvc.upload.injector.FileMetaInjector;
import org.nutz.mvc.upload.injector.InputStreamInjector;
import org.nutz.mvc.upload.injector.MapListInjector;
import org.nutz.mvc.upload.injector.MapSelfInjector;
import org.nutz.mvc.upload.injector.ReaderInjector;
import org.nutz.mvc.upload.injector.TempFileInjector;

/**
 * 本适配器专门处理 HTTP 文件上传(1.b.44及之后的版本,兼容Html5的流式上传)。 它支持多文件，多参数上传。具体的做法是将 HTTP
 * 上传的所有内容 包括文件以及名值对都预先缓存下来。其中，文件缓存在磁盘上，名值对缓存在内存中。
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
 * <p/>
 * <b>Html5流式上传(实验性)的注意事项:
 * 参数名默认是filedata,除非req.getHeader("Content-Disposition")中有描述另外的name</b>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 * 
 * @see org.nutz.mvc.annotation.Param
 */
public class UploadAdaptor extends PairAdaptor {
    private static final Log log = Logs.get();

    private UploadingContext context;

    public UploadAdaptor() throws IOException {
        context = new UploadingContext(File.createTempFile("nutz", null).getParent());
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

        if (null == param)
            return super.evalInjectorBy(type, null);

        String paramName = param.value();

        // File
        if (File.class.isAssignableFrom(clazz))
            return new FileInjector(paramName);
        // FileMeta
        if (FieldMeta.class.isAssignableFrom(clazz))
            return new FileMetaInjector(paramName);
        // TempFile
        if (TempFile.class.isAssignableFrom(clazz))
            return new TempFileInjector(paramName);
        // InputStream
        if (InputStream.class.isAssignableFrom(clazz))
            return new InputStreamInjector(paramName);
        // Reader
        if (Reader.class.isAssignableFrom(clazz))
            return new ReaderInjector(paramName);
        // List
        if (List.class.isAssignableFrom(clazz))
            return new MapListInjector(paramName);
        // Other
        return super.evalInjectorBy(type, param);
    }

    public Map<String, Object> getReferObject(ServletContext sc,
                                              HttpServletRequest request,
                                              HttpServletResponse response,
                                              String[] pathArgs) {
        try {
            if (!"POST".equals(request.getMethod()) && !"PUT".equals(request.getMethod())) {
                String str = "Not POST or PUT, Wrong HTTP method! --> " + request.getMethod();
                throw Lang.makeThrow(IllegalArgumentException.class, str);
            }
            // 看看是不是传统的上传
            String contentType = request.getContentType();
            if (contentType == null) {
                throw Lang.makeThrow(IllegalArgumentException.class, "Content-Type is NULL!!");
            }
            if (contentType.contains("multipart/form-data")) { // 普通表单上传
                if (log.isDebugEnabled())
                    log.debug("Select Html4 Form upload parser --> " + request.getRequestURI());
                Uploading ing = new FastUploading();
                return ing.parse(request, context);
            }
            if (contentType.contains("application/octet-stream")) { // Html5
                                                                    // 流式上传
                if (log.isDebugEnabled())
                    log.debug("Select Html5 Stream upload parser --> " + request.getRequestURI());
                Uploading ing = new Html5Uploading();
                return ing.parse(request, context);
            }
            // 100%是没写enctype='multipart/form-data'
            if (contentType.contains("application/x-www-form-urlencoded")) {
                log.warn("Using form upload ? You forgot this --> enctype='multipart/form-data' ?");
            }
            throw Lang.makeThrow(IllegalArgumentException.class, "Unknow Content-Type : "
                                                                 + contentType);
        }
        catch (UploadException e) {
            throw Lang.wrapThrow(e);
        }
        finally {
            Uploads.removeInfo(request);
        }
    }
}
