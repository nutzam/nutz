package org.nutz.mvc.view;

import org.nutz.ioc.Ioc;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;

/**
 * 默认的的视图工厂类
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class DefaultViewMaker implements ViewMaker {

	public View make(Ioc ioc, String type, String value) {
		if ("jsp".equals(type)) {
			return new JspView(value);
		} else if ("json".equals(type)) {
			if (Strings.isBlank(value)) {
				return new UTF8JsonView(JsonFormat.compact());
			} else {
				JsonFormat format = Json.fromJson(JsonFormat.class, value);
				return new UTF8JsonView(format);
			}
		} else if ("redirect".equals(type) || ">>".equals(type)) {
			return new ServerRedirectView(value);
		} else if ("void".equals(type)) {
			return new VoidView();
		} else if ("ioc".equals(type)) {
			return ioc.get(View.class, value);
		} else if ("http".equals(type)) {
			int sc = Integer.parseInt(value);
			return new HttpStatusView(sc);
		}
		return null;
	}

}
