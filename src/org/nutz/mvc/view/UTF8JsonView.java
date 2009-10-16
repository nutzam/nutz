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

public class UTF8JsonView implements View {

	private JsonFormat format;

	public UTF8JsonView(JsonFormat format) {
		this.format = format;
	}

	public void render(HttpServletRequest req, HttpServletResponse resp, Object obj) throws IOException {
		Writer writer = new BufferedWriter(new OutputStreamWriter(resp.getOutputStream(), "UTF-8"));
		Json.toJson(writer, obj, format);
		writer.close();
	}

}
