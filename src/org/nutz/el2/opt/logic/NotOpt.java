package org.nutz.el2.opt.logic;

import java.util.Queue;

import org.nutz.el2.opt.AbstractOpt;
import org.nutz.el2.opt.OptEnum;

/**
 * Not(!)
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class NotOpt extends AbstractOpt{
	private Object right;
	
	public int fetchPriority() {
		return 7;
	}
	public void wrap(Queue<Object> rpn){
		right = rpn.poll();
	}
	
	public Object calculate() {
		Object rval = calculateItem(this.right);
		if(rval instanceof Boolean){
			return !(Boolean) rval;
		}
		throw new RuntimeException();
	}
	
	public OptEnum fetchSelf() {
		return OptEnum.NOT;
	}
}
