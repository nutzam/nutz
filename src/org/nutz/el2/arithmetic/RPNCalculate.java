package org.nutz.el2.arithmetic;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

import org.nutz.el2.Operator;
import org.nutz.el2.obj.IdentifierObj;
import org.nutz.lang.util.Context;

/**
 * 逆波兰表达式计算
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class RPNCalculate {
	/**
	 * 根据逆波兰表达式进行计算
	 * @param rpn
	 * @return
	 */
	public Object calculate(Queue<Object> rpn){
		return calculate(null,rpn);
	}
	public Object calculate(Context context, Queue<Object> rpn) {
		Deque<Object> operand = OperatorTree(context, rpn);
		if(operand.peek() instanceof Operator){
			Operator obj = (Operator) operand.poll();
			return obj.calculate();
		}
		if(operand.peek() instanceof IdentifierObj){
			return ((IdentifierObj) operand.peek()).fetchVal();
		}
		return operand.poll();
	}
	/**
	 * 转换成操作树
	 * @param rpn
	 * @return
	 */
	private Deque<Object> OperatorTree(Context context, Queue<Object> rpn){
		Deque<Object> operand = new LinkedList<Object>();
		while(!rpn.isEmpty()){
			if(rpn.peek() instanceof Operator){
				Operator opt = (Operator) rpn.poll();
				opt.wrap(operand);
				operand.addFirst(opt);
				continue;
			}
			if(rpn.peek() instanceof IdentifierObj){
				((IdentifierObj) rpn.peek()).setContext(context);
			}
			operand.addFirst(rpn.poll());
		}
		return operand;
	}
	

}
