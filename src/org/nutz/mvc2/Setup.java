package org.nutz.mvc2;

import javax.servlet.ServletConfig;

public interface Setup {

	void init(ServletConfig config);

	void destroy(ServletConfig config);

}
