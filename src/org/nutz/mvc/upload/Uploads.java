package org.nutz.mvc.upload;

import java.util.Enumeration;

import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Mvcs;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * 关于上传的一些帮助函数
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public abstract class Uploads {

    /**
     * @param req
     *            请求对象
     * @return 当前会话的上传进度对象，如果没有上传，则返回 null
     */
    public static UploadInfo getInfo(HttpServletRequest req) {
        try {
            HttpSession session = Mvcs.getHttpSession(false);
            if (session == null) {
                return null;
            }
            return (UploadInfo) session.getAttribute(UploadInfo.SESSION_NAME);
        }
        catch (Throwable e) {}
        return null;
    }

    /**
     * @param req
     *            请求对象
     * @return 本次上传的进度对象
     */
    public static UploadInfo createInfo(HttpServletRequest req) {
        UploadInfo info = new UploadInfo();
        try {
            HttpSession sess = Mvcs.getHttpSession(false);
            if (null != sess) {
                sess.setAttribute(UploadInfo.SESSION_NAME, info);
            }
        }
        catch (Throwable e) {}
        info.sum = req.getContentLength();
        return info;
    }

    /**
     * 根据请求对象创建参数 MAP， 同时根据 QueryString，为 MAP 设置初始值
     * 
     * @param req
     *            请求对象
     * @return 参数 MAP
     */
    public static NutMap createParamsMap(HttpServletRequest req) {
        NutMap params = new NutMap();
        // parse query strings
        Enumeration<?> en = req.getParameterNames();
        while (en.hasMoreElements()) {
            String key = en.nextElement().toString();
            params.put(key, req.getParameter(key));
        }
        return params;
    }

    /**
     * 从当前会话中移除进度对象
     * 
     * @param req
     *            请求对象
     */
    public static void removeInfo(HttpServletRequest req) {
        try {
            HttpSession sess = req.getSession(false);
            if (null != sess) {
                sess.removeAttribute(UploadInfo.SESSION_NAME);
            }
        }
        catch (Throwable e) {}
    }

}
