package org.nutz.mvc;

import javax.servlet.http.HttpSession;

public interface SessionCallback {

	void process(HttpSession session);

}
