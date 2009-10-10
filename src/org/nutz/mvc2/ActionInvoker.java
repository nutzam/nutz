package org.nutz.mvc2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ActionInvoker {

	void invoke(HttpServletRequest request, HttpServletResponse response);

}
