package org.nutz.el2.opt.arithmetic;

import java.util.Queue;

import org.nutz.el2.opt.AbstractOpt;

/**
 * 负号:'-'
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class NegativeOpt extends AbstractOpt {
	private Object right;

	public int fetchPriority() {
		return 2;
	}

	public void wrap(Queue<Object> operand) {
		right = operand.poll();
	}

	public Object calculate() {
		Object rval = calculateItem(this.right);
		if(rval instanceof Double)
			return 0 - (Double)rval;
		if(rval instanceof Float)
			return 0 - (Float)rval;
		if(rval instanceof Long)
			return 0 - (Long)rval;
		return 0 - (Integer)rval;
	}

	public String fetchSelf() {
		return "-";
	}

}
