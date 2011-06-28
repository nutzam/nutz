package org.nutz.el.opt;

import java.util.Queue;

import org.nutz.el.Operator;

/**
 * 二元运算,只是提取了公共部分
 * @author juqkai(juqkai@gmail.com)
 *
 */
public abstract class TwoTernary extends AbstractOpt{
	protected Object right;
	protected Object left;
	
	public void wrap(Queue<Object> rpn){
		right = rpn.poll();
		left = rpn.poll();
	}

	public Object getRight() {
		if(right instanceof Operator){
			return ((Operator) right).calculate();
		}
		return right;
	}

	public Object getLeft() {
		if(left instanceof Operator){
			return ((Operator) left).calculate();
		}
		return left;
	}
}
