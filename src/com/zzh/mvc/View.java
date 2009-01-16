package com.zzh.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface View {

	void render(HttpServletRequest request, HttpServletResponse response, Object value)
			throws Exception;

	void setName(String name);

	String getName();

}
