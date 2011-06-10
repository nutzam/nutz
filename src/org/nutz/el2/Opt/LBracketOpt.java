package org.nutz.el2.Opt;

import java.util.Queue;

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
	
	public Object calculate(Queue<Object> obj) {
		throw new RuntimeException();
	}
	
}
