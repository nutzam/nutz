package org.nutz.mvc.view;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.nutz.el.El;
import org.nutz.el.ElValue;
import org.nutz.lang.segment.CharSegment;
import org.nutz.lang.segment.Segment;
import org.nutz.lang.stream.StringReader;
import org.nutz.lang.util.Context;
import org.nutz.lang.util.SimpleContext;
import org.nutz.mvc.Loading;
import org.nutz.mvc.impl.processor.ViewProcessor;

/**
 * 动态视图路径
 * @author juqkai (juqkai@gmail.com)
 * 2011-3-16
 */
public class DynamicViewPath {
	private Context context;
	@SuppressWarnings("unchecked")
	public DynamicViewPath(HttpServletRequest req, Object obj) {
		context = new SimpleContext();
		Object servletContext = req.getSession().getServletContext().getAttribute(Loading.CONTEXT_NAME);
		if(servletContext != null){
			context.putAll((Context)servletContext);
		}
		for(Object o : req.getParameterMap().keySet()){
			String key = (String) o;
			context.set(key, req.getParameter(key));
		}
		for(Enumeration<String> en = req.getAttributeNames();en.hasMoreElements();){
			String tem = en.nextElement();
			context.set(tem, req.getAttribute(tem));
		}
		context.set(ViewProcessor.DEFAULT_ATTRIBUTE, obj);
	}
	/**
	 * 解析路径,是 @Ok 中的完整路径,包括 ${} 表达式
	 */
	public String parsePath(String path){
		Segment seg = new CharSegment();
		seg.parse(new StringReader(path));
		return parsePath(seg);
	}
	public String parsePath(Segment seg){
		for(String key : seg.keys()){
			seg.set(key, parseEl(key));
		}
		return seg.toString();
	}
//	public String parsePath(String path){
//		int index = path.indexOf("${");
//		int end = path.indexOf("}");
//		if(index < 0 || end < 0){
//			return path;
//		}
//		StringBuilder sb = new StringBuilder();
//		sb.append(path.substring(0, index));
//		
//		String el = path.substring(index + 2, end);
//		sb.append(parseEl(el));
//		
//		sb.append(path.substring(end + 1));
//		return parsePath(sb.toString());
//	}
	String parseEl(String el){
		ElValue ev = El.eval(context, el);
		return ev.getString();
//		return (String) Array.get(ev.get(),0);
	}
}
