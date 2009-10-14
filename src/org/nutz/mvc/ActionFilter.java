package org.nutz.mvc;

import javax.servlet.http.HttpServletRequest;

public interface ActionFilter {

	View match(HttpServletRequest request);

}
