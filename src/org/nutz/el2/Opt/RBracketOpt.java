package org.nutz.el2.Opt;

import java.util.Queue;

/**
 * 右括号')'
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class RBracketOpt extends AbstractOpt{

	public int fetchPriority() {
		return 100;
	}
	public OptEnum fetchSelf() {
		return OptEnum.RBRACKET;
	}
	public Object calculate(Queue<Object> obj) {
		throw new RuntimeException();
	}

}
