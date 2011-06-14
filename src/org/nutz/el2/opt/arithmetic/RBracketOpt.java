package org.nutz.el2.opt.arithmetic;

import java.util.Queue;

import org.nutz.el2.opt.AbstractOpt;

/**
 * 右括号')'
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class RBracketOpt extends AbstractOpt{

	public int fetchPriority() {
		return 100;
	}
	public String fetchSelf() {
		return ")";
	}
	public void wrap(Queue<Object> obj) {
		throw new RuntimeException();
	}
	public Object calculate() {
		throw new RuntimeException();
	}

}
