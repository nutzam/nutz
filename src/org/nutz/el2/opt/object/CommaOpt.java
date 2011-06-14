package org.nutz.el2.opt.object;

import java.util.Queue;

import org.nutz.el2.opt.AbstractOpt;

/**
 * ","
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class CommaOpt extends AbstractOpt {
	public int fetchPriority() {
		return 0;
	}

	public void wrap(Queue<Object> operand) {
	}
	public Object calculate() {
		return null;
	}
	public String fetchSelf() {
		return ",";
	}

}
