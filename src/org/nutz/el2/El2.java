package org.nutz.el2;

import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

import org.nutz.el2.arithmetic.ShuntingYard;

public class El2 {

	/**
	 * 对参数代表的表达式进行运算
	 * @param val
	 * @return
	 */
	public Object eval(String val) {
		try {
			//逆波兰表示法（Reverse Polish notation，RPN，或逆波兰记法）
			ShuntingYard sy = new ShuntingYard();
			Queue<Object> RPN = sy.parseToRPN(val);
			return RPNCalculate(RPN);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private Object RPNCalculate(Queue<Object> rpn){
		Deque<Object> operand = new LinkedList<Object>();
		while(!rpn.isEmpty()){
			if(rpn.peek() instanceof Operator){
				Operator opt = (Operator) rpn.poll();
				operand.addFirst(opt.calculate(operand));
				continue;
			}
			operand.addFirst(rpn.poll());
		}
		return operand.poll();
	}
	
	//@ TODO 负数参与运算时没有进行处理如: -1+1
	//@ TODO 添加对象转换,以及对象方法调用
	//@ TODO 添加变量支持
	//@ TODO 在变量基础上,添加数组支持
}
