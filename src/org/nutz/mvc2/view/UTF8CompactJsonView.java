package org.nutz.mvc2.view;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.mvc2.View;

public class UTF8CompactJsonView implements View {

	private static JsonFormat format = JsonFormat.compact();

	public UTF8CompactJsonView() {}

	public void render(HttpServletRequest request, HttpServletResponse response, Object obj)
			throws IOException {
		Writer writer = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(),
				"UTF-8"));
		Json.toJson(writer, obj, format);
		writer.close();
	}

}
