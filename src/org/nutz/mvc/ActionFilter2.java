package org.nutz.mvc;

/**
 * 扩展ActionFilter接口,使其具备在方法执行后进行操作的能力
 * @author wendal
 *
 */
public interface ActionFilter2 extends ActionFilter {

	/**
	 * 方法执行完成后操作
	 */
	void after(ActionContext ctx);
}
