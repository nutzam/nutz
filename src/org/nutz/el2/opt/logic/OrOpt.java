package org.nutz.el2.opt.logic;

import java.util.Queue;

import org.nutz.el2.opt.AbstractOpt;
import org.nutz.el2.opt.OptEnum;

/**
 * or(||)
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class OrOpt extends AbstractOpt{
	private Object right;
	private Object left;
	
	public int fetchPriority() {
		return 12;
	}
	public void wrap(Queue<Object> operand) {
		right = operand.poll();
		left = operand.poll();
	}
	public Object calculate() {
		Object lval = calculateItem(left);
		if(!(lval instanceof Boolean)){
			throw new RuntimeException("操作数类型错误!");
		}
		if((Boolean)lval){
			return true;
		}
		Object rval = calculateItem(right);
		if(!(rval instanceof Boolean)){
			throw new RuntimeException("操作数类型错误!");
		}
		if((Boolean)rval){
			return true;
		}
		return false;
	}
	public OptEnum fetchSelf() {
		return OptEnum.OR;
	}

}
