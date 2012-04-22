package org.nutz.mvc.impl;

import org.junit.Test;
import org.nutz.mock.servlet.MockFilterConfig;
import org.nutz.mock.servlet.MockServletContext;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.config.FilterNutConfig;

public class NutLoadingImplTest {

	
	@Test
	public void testUrlMapping() throws Exception{
		MockFilterConfig mfc = new MockFilterConfig();
		MockServletContext msc = new MockServletContext();
		msc.setServletContextName("servlet");
		mfc.setServletContext(msc);
		Mvcs.setServletContext(msc);
		mfc.addInitParameter("modules", "controllers.MainModule");
		FilterNutConfig nc = new FilterNutConfig(mfc) ;
		
		new NutLoading2().load(nc);
		
	}
}
