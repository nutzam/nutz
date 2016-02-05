package org.nutz.mvc;

/**
 * 扩展ActionFilter接口,使其具备类似aop的操作能力
 * @author wendal
 *
 */
public interface ActionFilter2 extends ActionFilter {

	/**
	 * 方法执行完成后操作
	 */
	void after(ActionContext ctx) throws Throwable;
	
	/**
	 * 抛出异常时执行
	 * @param ctx 执行上下文
	 * @param e 异常对象
	 * @return 是否继续抛出异常,true抛出, false不抛出
	 */
	boolean whenError(ActionContext ctx, Throwable e) throws Throwable;
}
