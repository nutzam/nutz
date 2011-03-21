package org.nutz.mvc.view;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.nutz.el.El;
import org.nutz.el.ElObj;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.CharSegment;
import org.nutz.lang.segment.Segment;
import org.nutz.lang.util.Context;
import org.nutz.mvc.Loading;
import org.nutz.mvc.View;
import org.nutz.mvc.impl.processor.ViewProcessor;

/**
 * @author mawm(ming300@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
public abstract class AbstractPathView implements View {

	private Segment dest;

	private Map<String, ElObj> exps;

	public AbstractPathView(String dest) {
		this.dest = new CharSegment(Strings.trim(dest));
		this.exps = new HashMap<String, ElObj>();
		// 预先将每个占位符解析成表达式
		for (String key : this.dest.keys()) {
			this.exps.put(key, El.compile(key));
		}
	}

	protected String evalPath(HttpServletRequest req, Object obj) {
		Context context = Lang.context();

		// 解析每个表达式
		Context expContext = createContext(req, obj);
		for (Entry<String, ElObj> en : exps.entrySet())
			context.set(en.getKey(), en.getValue().eval(expContext).getString());

		// 生成解析后的路径
		return this.dest.render(context).toString();
	}

	/**
	 * 为一次 HTTP 请求，创建一个可以被表达式引擎接受的上下文对象
	 * 
	 * @param req
	 *            HTTP 请求对象
	 * @param obj
	 *            入口函数的返回值
	 * @return 上下文对象
	 */
	@SuppressWarnings("unchecked")
	public static Context createContext(HttpServletRequest req, Object obj) {
		Context context = Lang.context();
		// 复制全局的上下文对象
		Object servletContext = req.getSession()
									.getServletContext()
									.getAttribute(Loading.CONTEXT_NAME);
		if (servletContext != null) {
			context.putAll((Context) servletContext);
		}
		// 请求的参数表
		for (Object o : req.getParameterMap().keySet()) {
			String key = (String) o;
			context.set(key, req.getParameter(key));
		}
		// 请求对象的属性列表
		for (Enumeration<String> en = req.getAttributeNames(); en.hasMoreElements();) {
			String tem = en.nextElement();
			context.set(tem, req.getAttribute(tem));
		}
		// 加入返回对象
		if (null != obj)
			context.set(ViewProcessor.DEFAULT_ATTRIBUTE, obj);
		return context;
	}
}
