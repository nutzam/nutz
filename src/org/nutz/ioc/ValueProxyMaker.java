package org.nutz.ioc;

import org.nutz.ioc.meta.IocValue;

/**
 * 本接口设计用来提供容器支持更多种类的值的类型。 你可以通过 Ioc 接口，增加你自定义的 ValueProxyMaker，你自定义的
 * ValueProxyMaker 会 比容器内内置的 ValueProxyMaker 更优先。即，后加入优先
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 * @see org.nuzt.ioc.Ioc2
 */
public interface ValueProxyMaker {

	/**
	 * 如果返回的是 null，则表示当前的实现不能解释这个 IocValue，容器会调用下一个 ValueProxyMaker 来做解析
	 * 
	 * @param iv
	 *            字段装配信息
	 * @param ing
	 *            对象装配时
	 * 
	 * @return 值代理对象
	 */
	ValueProxy make(IocMaking ing, IocValue iv);

}
