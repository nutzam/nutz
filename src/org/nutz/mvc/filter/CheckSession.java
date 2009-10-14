package org.nutz.mvc.filter;

import javax.servlet.http.HttpServletRequest;

import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.View;
import org.nutz.mvc.view.ServerRedirectView;

/**
 * 检查当前 Session，如果存在某一属性，并且不为 null，则通过 <br>
 * 否则，返回一个 ServerRecirectView 到对应 path
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class CheckSession implements ActionFilter {

	private String name;
	private String path;

	public CheckSession(String name, String path) {
		this.name = name;
		this.path = path;
	}

	public View match(HttpServletRequest request) {
		Object obj = request.getSession().getAttribute(name);
		if (null == obj)
			return new ServerRedirectView(path);
		return null;
	}

}
