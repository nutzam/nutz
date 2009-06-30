package org.nutz.mvc.localize;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.Controllor;
import org.nutz.mvc.Return;

public class SetLocalization implements Controllor {
	
	@Override
	public Object execute(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String name = request.getParameter("name");
		Localizations.setLocalization(request.getSession(),name);
		return Return.OK();
	}

}
