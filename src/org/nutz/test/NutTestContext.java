package org.nutz.test;

import org.nutz.ioc.Ioc;
import org.nutz.lang.Mirror;

public class NutTestContext {

	private static final NutTestContext me = new NutTestContext();
	
	public Ioc ioc;
	
	@SuppressWarnings("rawtypes")
	public Mirror mirror;
	
	private NutTestContext() {}
	
	public static final NutTestContext me() {
		return me;
	}
	
	
}
