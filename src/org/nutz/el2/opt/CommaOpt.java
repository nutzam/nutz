package org.nutz.el2.opt;

import java.util.Queue;

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
	public OptEnum fetchSelf() {
		return OptEnum.COMMA;
	}

}
