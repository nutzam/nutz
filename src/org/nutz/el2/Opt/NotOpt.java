package org.nutz.el2.Opt;

import java.util.Queue;

public class NotOpt extends AbstractOpt{
	public int fetchPriority() {
		return 6;
	}
	public Object calculate(Queue<Object> rpn){
		Object right = rpn.poll();
		if(right instanceof Boolean){
			return !(Boolean) right;
		}
		throw new RuntimeException();
	}
	public OptEnum fetchSelf() {
		return OptEnum.NOT;
	}
}
