package org.nutz.el2.Opt;

import java.util.Queue;

public class NEQOpt extends AbstractOpt{
	public int fetchPriority() {
		return 6;
	}
	public Object calculate(Queue<Object> rpn){
		Object right = rpn.poll();
		Object left = rpn.poll();
		if(right instanceof Double || left instanceof Double){
			return (Double)left != (Double)right;
		}
		if(right instanceof Float || left instanceof Float){
			return (Float)left != (Float)right;
		}
		if(right instanceof Long || left instanceof Long){
			return (Long)left != (Long)right;
		}
		return (Integer)left != (Integer)right;
	}
	public OptEnum fetchSelf() {
		return OptEnum.NEQ;
	}

}
