package org.nutz.el2.opt.arithmetic;

import java.util.Queue;

import org.nutz.el2.opt.AbstractOpt;
import org.nutz.el2.opt.OptEnum;

/**
 * "+"
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class PlusOpt extends AbstractOpt {
	private Object right;
	private Object left;

	public int fetchPriority() {
		return 4;
	}

	public OptEnum fetchSelf() {
		return OptEnum.PLUS;
	}

	public void wrap(Queue<Object> rpn){
		right = rpn.poll();
		left = rpn.poll();
	}
	
	public Object calculate() {
		Number lval = (Number) calculateItem(this.left);
		Number rval = (Number) calculateItem(this.right);
		if(rval instanceof Double || lval instanceof Double){
			return lval.doubleValue() + rval.doubleValue();
		}
		if(rval instanceof Float || lval instanceof Float){
			return lval.floatValue() + rval.floatValue();
		}
		if(rval instanceof Long || lval instanceof Long){
			return lval.longValue() + rval.longValue();
		}
		return lval.intValue() + rval.intValue();
	}

}
