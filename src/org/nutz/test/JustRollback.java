package org.nutz.test;

@SuppressWarnings("serial")
public class JustRollback extends RuntimeException {
	
	private static final JustRollback me = new JustRollback();
	
	public static final JustRollback me() {
		return me;
	}
}