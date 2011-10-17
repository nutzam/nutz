package org.nutz.test;

/**
 * 类名如其功能,抛异常,回滚吧!!
 * @author wendal(wendal1985@gmail.com)
 *
 */
@SuppressWarnings("serial")
class JustRollback extends RuntimeException {
	
	private static final JustRollback me = new JustRollback();
	
	private JustRollback() {}
	
	public static final JustRollback me() {
		return me;
	}
}