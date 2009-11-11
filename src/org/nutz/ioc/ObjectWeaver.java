package org.nutz.ioc;

public interface ObjectWeaver {

	/**
	 * 编织一个对象，如果你的实现缓存了对象，直接返回
	 */
	Object weave(IocMaking ing);

	/**
	 * 释放一个对象。如果你的实现没有缓存对象，什么都不用做
	 */
	void deose();

}
