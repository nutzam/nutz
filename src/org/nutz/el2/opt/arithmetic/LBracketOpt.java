package org.nutz.el2.opt.arithmetic;

import java.util.Queue;

import org.nutz.el2.opt.AbstractOpt;

/**
 * "("
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class LBracketOpt extends AbstractOpt{
	public String fetchSelf() {
		return "(";
	}
	public int fetchPriority() {
		return 100;
	}
	
	public void wrap(Queue<Object> obj) {
		throw new RuntimeException();
	}
	public Object calculate() {
		throw new RuntimeException();
	}
}
