package org.nutz.el2.opt.logic;

import java.util.Queue;

import org.nutz.el2.opt.AbstractOpt;
import org.nutz.el2.opt.OptEnum;

/**
 * and
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class AndOpt extends AbstractOpt {
	private Object right;
	private Object left;
	
	public int fetchPriority() {
		return 11;
	}

	public void wrap(Queue<Object> operand) {
		right = operand.poll();
		left = operand.poll();
	}
	
	public Object calculate() {
		Object lval = calculateItem(this.left);
		if(!(lval instanceof Boolean)){
			throw new RuntimeException("操作数类型错误!");
		}
		if(!(Boolean)lval){
			return false;
		}
		Object rval = calculateItem(this.right);
		if(!(rval instanceof Boolean)){
			throw new RuntimeException("操作数类型错误!");
		}
		if(!(Boolean)rval){
			return false;
		}
		return true;
	}

	public OptEnum fetchSelf() {
		return OptEnum.AND;
	}

}
