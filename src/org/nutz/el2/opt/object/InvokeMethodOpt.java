package org.nutz.el2.opt.object;

import java.util.Queue;

import org.nutz.el2.opt.AbstractOpt;

/**
 * 执行
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class InvokeMethodOpt extends AbstractOpt {
	private Object left;

	public int fetchPriority() {
		return 1;
	}

	public Object calculate() {
		if(left instanceof ListOpt){
			return ((ListOpt) left).calculate();
		}
		return null;
	}

	public String fetchSelf() {
		return "invoke";
	}

	public void wrap(Queue<Object> operand) {
		left = operand.poll();
	}

}
