package org.nutz.mvc.view;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.mvc.View;

/**
 * 将数据采用json方式输出的试图实现
 *
 * @author zozoh(zozohtnt@gmail.com)
 * @author mawn(ming300@gmail.com)
 */
public class UTF8JsonView implements View {

	private JsonFormat format;

	private Object data;

	public void setData(Object data) {
		this.data = data;
	}

	public UTF8JsonView(JsonFormat format) {
		this.format = format;
	}

	public void render(HttpServletRequest req, HttpServletResponse resp, Object obj)
			throws IOException {
		// by mawm 改为直接采用resp.getWriter()的方式直接输出!
		resp.setHeader("Cache-Control", "no-cache");
		resp.setContentType("text/json;charset=UTF-8");
		
		if (null == obj)
			obj = data;
		Json.toJson(resp.getWriter(), obj, format);
		
		resp.flushBuffer();
	}
}
