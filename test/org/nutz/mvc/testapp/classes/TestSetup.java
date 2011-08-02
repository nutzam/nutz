package org.nutz.mvc.testapp.classes;

import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

public class TestSetup implements Setup {

	@Override
	public void init(NutConfig config) {
		System.out.println(config.getAtMap().size());
	}

	@Override
	public void destroy(NutConfig config) {
		
	}

}
