package org.nutz.el2;

import java.util.Queue;

/**
 * 操作符
 * @author juqkai(juqkai@gmail.com)
 *
 */
public interface Operator {

	/**
	 * 优先级
	 * @return
	 */
	public int fetchPriority();

	/**
	 * 计算
	 * @param operand 操作数
	 * @return
	 */
	public Object calculate(Queue<Object> operand);

}
