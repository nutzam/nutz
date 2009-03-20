package com.zzh.mvc.view;

import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zzh.json.Json;
import com.zzh.json.JsonFormat;
import com.zzh.mvc.View;

public class JsonView implements View {

	private static JsonFormat defaultFormat = JsonFormat.compact();

	public JsonView() {}

	public JsonView(JsonFormat format) {
		this.format = format;
	}

	private JsonFormat format;
	public String charset;

	@Override
	public void render(HttpServletRequest request, HttpServletResponse response, Object obj)
			throws IOException {
		OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream(),
				null == charset ? "UTF-8" : charset);
		String json = Json.toJson(obj, null == format ? defaultFormat : format);
		writer.write(json);
		writer.close();
	}

}
