package org.nutz.el2.Opt;

import java.util.Queue;

/**
 * 除
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class DivOpt extends AbstractOpt {

	public int fetchPriority() {
		return 3;
	}

	public Object calculate(Queue<Object> rpn){
		Number right = (Number) rpn.poll();
		Number left = (Number) rpn.poll();
		if(right instanceof Double || left instanceof Double){
			
			return left.doubleValue() / right.doubleValue();
		}
		if(right instanceof Float || left instanceof Float){
			return (Float)left / (Float)right;
		}
		if(right instanceof Long || left instanceof Long){
			return (Long)left / (Long)right;
		}
		return (Integer)left / (Integer)right;
	}

	public OptEnum fetchSelf() {
		return OptEnum.DIV;
	}

}
