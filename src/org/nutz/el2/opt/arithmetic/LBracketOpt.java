package org.nutz.el2.opt.arithmetic;

import java.util.Queue;

import org.nutz.el2.opt.AbstractOpt;
import org.nutz.el2.opt.OptEnum;

/**
 * "("
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class LBracketOpt extends AbstractOpt{
	public OptEnum fetchSelf() {
		return OptEnum.LBRACKET;
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
