package org.nutz.mvc;

import javax.servlet.ServletConfig;

public interface Setup {

	void init(ServletConfig config);

	void destroy(ServletConfig config);

}
