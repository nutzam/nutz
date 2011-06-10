package org.nutz.el2.Opt;

import java.util.Queue;

/**
 * "-"
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class SubOpt extends AbstractOpt{

	public OptEnum fetchSelf() {
		return OptEnum.SUB;
	}
	public int fetchPriority() {
		return 4;
	}
	public Object calculate(Queue<Object> rpn){
		Object right = rpn.poll();
		Object left = rpn.poll();
		if(right instanceof Double || left instanceof Double){
			return (Double)left - (Double)right;
		}
		if(right instanceof Float || left instanceof Float){
			return (Float)left - (Float)right;
		}
		if(right instanceof Long || left instanceof Long){
			return (Long)left - (Long)right;
		}
		return (Integer)left - (Integer)right;
	}

}
