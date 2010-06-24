package org.nutz.mvc.upload;

import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.nutz.lang.util.NutMap;

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
		return (UploadInfo) req.getSession().getAttribute(UploadInfo.class.getName());
	}

	/**
	 * @param req
	 *            请求对象
	 * @return 本次上传的进度对象
	 */
	public static UploadInfo createInfo(HttpServletRequest req) {
		UploadInfo info = new UploadInfo();
		HttpSession sess = req.getSession();
		if (null != sess) {
			sess.setAttribute(UploadInfo.SESSION_NAME, info);
		}
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
	@SuppressWarnings("unchecked")
	public static NutMap createParamsMap(HttpServletRequest req) {
		NutMap params = new NutMap();
		// parse query strings
		Map<String, String []> paramsZ = req.getParameterMap();
		if (null != paramsZ && paramsZ.size() >0  ){
			for (Entry<String, String []> ppp : paramsZ.entrySet()) {
				if (ppp.getValue() != null ) {
					if (ppp.getValue().length > 0)
						params.put(ppp.getKey(), ppp.getValue()[0]);
					else
						params.put(ppp.getKey(), "");
				} else
					params.put(ppp.getKey(), null);
			}
		}
		return params;
	}

}
