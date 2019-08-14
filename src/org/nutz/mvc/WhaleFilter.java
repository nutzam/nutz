package org.nutz.mvc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.nutz.filepool.UU32FilePool;
import org.nutz.lang.Each;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.LogAdapter;
import org.nutz.log.Logs;
import org.nutz.mvc.upload.FastUploading;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadException;
import org.nutz.mvc.upload.UploadingContext;

public class WhaleFilter implements Filter {

    protected Properties props = new Properties();
    protected String inputEnc;
    protected String outputEnc;
    protected String methodParam;
    protected boolean allowHTTPMethodOverride;
    protected boolean uploadEnable;
    private static WhaleFilter _me;
    protected ServletContext sc;
    protected Object uc;

    public static WhaleFilter me() {
        return _me;
    }

    public void init(FilterConfig c) throws ServletException {
        sc = c.getServletContext();
        _me = this;
        try {
            Enumeration<String> keys = c.getInitParameterNames();
            while (keys.hasMoreElements()) {
                String key = keys.nextElement();
                String value = c.getInitParameter(key);
                if (!Strings.isBlank(value) && !"null".equals(value))
                    props.put(key, c.getInitParameter(key));
            }
            String path = c.getInitParameter("config-file");
            if (path != null) {
                InputStream ins = getClass().getClassLoader().getResourceAsStream(path);
                if (ins == null)
                    ins = c.getServletContext().getResourceAsStream(path);
                if (ins == null) {
                    throw new ServletException("config-file=" + path + " not found");
                }
                init(ins);
            } else {
                String config = c.getInitParameter("config");
                if (config != null) {
                    init(new ByteArrayInputStream(config.getBytes()));
                }
                else {
                    init((InputStream)null);
                }
            }
        }
        catch (Exception e) {
            throw new ServletException(e);
        }
    }

    public void init(InputStream ins) throws Exception {
        if (ins != null)
            props.load(ins);
        if (props.containsKey("log.adapter")) {
            LogAdapter la = (LogAdapter) Class.forName(props.getProperty("log.adapter")).newInstance();
            Logs.setAdapter(la);
        }
        inputEnc = props.getProperty("enc.input");
        outputEnc = props.getProperty("enc.output");
        methodParam = props.getProperty("http.hidden_method_param");
        allowHTTPMethodOverride = "true".equals(props.getProperty("http.method_override"));
        uploadEnable = "true".equals(props.getProperty("upload.enable"));
        if (uploadEnable) {
            String tmpPath = props.getProperty("upload.tmpdir", System.getProperty("java.io.tmpdir") + "/whale");
            UU32FilePool fp = new UU32FilePool(tmpPath);
            UploadingContext uc = new UploadingContext(fp);
            Mirror<UploadingContext> mirror = Mirror.me(uc);
            for (Object _key : props.keySet()) {
                String key = _key.toString();
                if (!key.startsWith("upload.")) {
                    continue;
                }
                key = key.substring("upload.".length());
                if ("tmpdir".equals(key) || "exclusions".equals(key) || "enable".equals(key)) {
                    continue;
                }
                mirror.setValue(uc, key, props.get(_key));
            }
            this.uc = uc;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        final HttpServletRequest req = (HttpServletRequest) request;
        final HttpServletResponse resp = (HttpServletResponse) response;

        // 设置req的编码
        if (inputEnc != null)
            req.setCharacterEncoding(inputEnc);
        // 设置resp的编码
        if (outputEnc != null)
            resp.setCharacterEncoding(outputEnc);

        // 如果是POST请求,有很多可以hack的东西
        if ("POST".equals(req.getMethod())) {
            // 处理隐藏HTTP METHOD, _method参数模式
            if (methodParam != null) {
                String qs = req.getQueryString();
                if (qs != null && qs.contains("_method=")) {
                    final NutMap map = Mvcs.toParamMap(new StringReader(qs), inputEnc == null ? Charset.defaultCharset().name() : inputEnc);
                    request = new HttpServletRequestWrapper(req) {
                        public String getMethod() {
                            return map.getString(methodParam);
                        }
                    };
                }
            }
            // 处理 X-HTTP-Method-Override
            else if (allowHTTPMethodOverride && req.getHeader("X-HTTP-Method-Override") != null) {
                request = new HttpServletRequestWrapper(req) {
                    public String getMethod() {
                        return req.getHeader("X-HTTP-Method-Override");
                    }
                };
            }

            // 处理文件上传
            String contentType = req.getContentType();
            if (contentType != null) {
                if (uc != null && contentType.contains("multipart/form-data")) {
                    request = handleUpload(req);
                }
            }
        }

        try {
            chain.doFilter(request, response);
        } finally {
            try {
                List<TempFile> files = (List<TempFile>) req.getAttribute("_files");
                if (files != null) {
                    for (TempFile tf : files) {
                        if (tf != null)
                            tf.delete();
                    }
                }
            }
            catch (Exception e) {
            }
        }

    }

    public void destroy() {}

    @SuppressWarnings("unchecked")
    public HttpServletRequest handleUpload(HttpServletRequest req) throws ServletException {
        try {
            FastUploading fup = new FastUploading();
            final Map<String, Object> params = fup.parse(req, (UploadingContext) uc);
            final List<TempFile> files = new ArrayList<TempFile>();
            Iterator<Entry<String, Object>> it = params.entrySet().iterator();
            while (it.hasNext()) {
                Object obj = it.next().getValue();
                final boolean[] re = new boolean[1];
                Lang.each(obj, new Each<Object>() {
                    public void invoke(int index, Object ele, int length){
                        if (ele != null && ele instanceof TempFile) {
                            files.add((TempFile) ele);
                            re[0] = true;
                        }
                    }
                });
                if (re[0])
                    it.remove();
            }
            req.setAttribute("_files", files);
            params.putAll(req.getParameterMap());
            return new HttpServletRequestWrapper(req) {
                public String getParameter(String name) {
                    return (String) params.get(name);
                }
                @SuppressWarnings("rawtypes")
                public Map getParameterMap() {
                    return params;
                }
                @SuppressWarnings("rawtypes")
                public Enumeration getParameterNames() {
                    return Collections.enumeration(params.keySet());
                }
                public String[] getParameterValues(String name) {
                    if (params.containsKey(name))
                        return new String[]{(String) params.get(name)};
                    return null;
                }
            };
        }
        catch (UploadException e) {
            throw new ServletException("upload fail", e);
        }
    }
}
