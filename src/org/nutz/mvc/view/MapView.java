package org.nutz.mvc.view;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.ioc.Ioc;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;

/**
 * 根据返回值来选择适当的视图进行渲染<p/>
 * 使用方法:<p/>
 * <code>@Ok("map:{success:'>>:/index.html',fail:'>>:/login.html',default :'>>:/'}")</code><p/>
 * 这个视图的类型值为map<p/>
 * 其值应为应该json格式的Map,每个key对应一种普通视图的配置<p/>
 * 默认视图,如果返回值没有对应的视图,则使用key为null或者default的视图,如果均不存在,则使用VoidView
 * @author wendal
 *
 */
public class MapView implements View {
	
	private Map<String, View> map;
	private View defaultView;
	
	@SuppressWarnings("unchecked")
	public MapView(String value, ViewMaker viewMaker, Ioc ioc) {
		if (Strings.isBlank(value)) {
			map = Collections.EMPTY_MAP;
			defaultView = new VoidView();
		} else {
			Map<String, Object> mapping = Json.fromJson(Map.class, value);
			map = new HashMap<String, View>(mapping.size());
			for (Entry<String,Object> entry : mapping.entrySet()) {
				String str = String.valueOf(entry.getValue());
				int pos = str.indexOf(':');
				String type, value2;
				if (pos > 0) {
					type = Strings.trim(str.substring(0, pos).toLowerCase());
					value2 = Strings.trim(pos >= (str.length() - 1) ? null : str.substring(pos + 1));
				} else {
					type = str;
					value2 = null;
				}
				map.put(entry.getKey(), viewMaker.make(ioc, type, value2));
			}
			defaultView = map.get("null");
			if (defaultView == null)
				defaultView = map.get("default");
			if (defaultView == null)
				defaultView = new VoidView();
		}
	}

	public void render(HttpServletRequest req, HttpServletResponse resp,
			Object obj) throws Throwable {
		if (null != obj) {
			View view = map.get(String.valueOf(obj));
			if (view != null) {
				view.render(req, resp, obj);
				return;
			}
		}
		defaultView.render(req, resp, obj);
	}

}
