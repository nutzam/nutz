package org.nutz.mock.servlet;

import org.nutz.http.impl.servlet.NutHttpConfig;

/**
 * 模拟FilterConfig
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class MockFilterConfig extends NutHttpConfig {

	public void setFilterName(String filterName) {
		this.name = filterName;
	}
}
