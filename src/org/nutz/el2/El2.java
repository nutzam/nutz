package org.nutz.el2;

import java.util.Queue;

import org.nutz.el2.arithmetic.RPNCalculate;
import org.nutz.el2.arithmetic.ShuntingYard;
import org.nutz.lang.util.Context;

public class El2 {
	private ShuntingYard sy = new ShuntingYard();
	private RPNCalculate rc = new RPNCalculate();

	/**
	 * 对参数代表的表达式进行运算
	 * @param val
	 * @return
	 */
	public Object eval(String val) {
		//逆波兰表示法（Reverse Polish notation，RPN，或逆波兰记法）
		return eval(null, val);
	}

	public Object eval(Context context, String val) {
		Queue<Object> rpn = sy.parseToRPN(val);
		return rc.calculate(context, rpn);
	}
	
	//@ JKTODO abc(1+(1-1)),不知道这个函数请求会不会出错
}
