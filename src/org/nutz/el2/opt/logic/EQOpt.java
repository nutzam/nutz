package org.nutz.el2.opt.logic;

import java.util.Queue;

import org.nutz.el2.opt.AbstractOpt;
import org.nutz.el2.opt.OptEnum;

/**
 * 等于
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class EQOpt extends AbstractOpt {
	private Object right;
	private Object left;
	
	public int fetchPriority() {
		return 7;
	}
	public void wrap(Queue<Object> rpn){
		right = rpn.poll();
		left = rpn.poll();
	}
	
	public Object calculate() {
		Object lval = calculateItem(this.left);
		Object rval = calculateItem(this.right);
		return lval == rval;
	}
	
	public OptEnum fetchSelf() {
		return OptEnum.EQ;
	}
}
