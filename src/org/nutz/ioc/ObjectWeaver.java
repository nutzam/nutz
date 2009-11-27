package org.nutz.ioc;

public interface ObjectWeaver {

	/**
	 * 编织一个对象
	 * 
	 * @param ing
	 *            对象注入时
	 * @return 编织后对象
	 */
	Object weave(IocMaking ing);

	/**
	 * 释放一个对象。如果你的实现没有缓存对象，什么都不用做
	 */
	void depose();

}
