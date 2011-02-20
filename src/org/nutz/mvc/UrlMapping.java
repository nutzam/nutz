package org.nutz.mvc;

import javax.servlet.http.HttpServletRequest;

/**
 * 路径映射
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface UrlMapping {

	/**
	 * 增加一个映射
	 * 
	 * @param maker
	 *            处理器工厂
	 * @param ai
	 *            处理器配置
	 */
	void add(ActionChainMaker maker, ActionInfo ai, NutConfig config);

	/**
	 * 根据一个路径，获取一个处理器，并且，如果这个路径中包括统配符 '?' 或者 '*' <br>
	 * 需要为上下文对象设置好路径参数
	 * 
	 * @param ac
	 *            上下文对象
	 * @param path
	 *            路径
	 * @return 处理器链表头节点
	 */
	ActionChain get(ActionContext ac, HttpServletRequest req);

}
