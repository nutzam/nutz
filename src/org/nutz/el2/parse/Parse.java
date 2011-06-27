package org.nutz.el2.parse;

import java.util.Queue;

public interface Parse {

	/**
	 * 提取队列顶部元素
	 * @param exp 表达式
	 * @return
	 */
	Object fetchItem(Queue<Character> exp);

}
