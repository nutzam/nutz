package org.nutz.ioc.aop;

import org.nutz.lang.Mirror;

public interface MirrorFactory {

	/**
	 * 根据一个类型生成 Mirror 对象。
	 * <p>
	 * 如果你在类中某些声明了 '@Aop' 注解，那么这个类将被这个接口解析 并重新制作一个子类<br>
	 * 用来拦截方法
	 */
	<T> Mirror<T> getMirror(Class<T> type, String name);

}
