package com.zzh.mvc.v;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zzh.json.Json;
import com.zzh.json.JsonFormat;
import com.zzh.mvc.MvcUtils;

public class JsonView extends AbstractView {

	public JsonView() {
		super();
	}

	public JsonView(String name) {
		super(name);
	}

	private JsonFormat format;

	public JsonFormat getFormat() {
		return format;
	}

	public void setFormat(JsonFormat format) {
		this.format = format;
	}

	@Override
	public void render(HttpServletRequest request, HttpServletResponse response, Object value)
			throws Exception {
		response.getWriter().write(Json.toJson(value, format, MvcUtils.getCastors(request)));
		response.flushBuffer();
	}

}
