package org.nutz.mock.servlet;

import javax.servlet.FilterConfig;

/**
 * 模拟FilterConfig
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class MockFilterConfig extends MockServletObject implements FilterConfig {
    
    private String filterName;

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }
}
