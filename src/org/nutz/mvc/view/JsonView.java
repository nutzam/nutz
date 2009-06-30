package org.nutz.mvc.view;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.mvc.View;

public class JsonView implements View {

	private static JsonFormat defaultFormat = JsonFormat.compact();

	public JsonView() {}

	public JsonView(JsonFormat format) {
		this.format = format;
	}

	private JsonFormat format;
	private String charset;

	@Override
	public void render(HttpServletRequest request, HttpServletResponse response, Object obj)
			throws IOException {
		Writer writer = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(),
				null == charset ? "UTF-8" : charset));
		Json.toJson(writer, obj, null == format ? defaultFormat : format);
		writer.close();
	}

}
