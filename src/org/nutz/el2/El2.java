package org.nutz.el2;

import java.io.IOException;
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
		try {
			//逆波兰表示法（Reverse Polish notation，RPN，或逆波兰记法）
			
			Queue<Object> rpn = sy.parseToRPN(val);
			return rc.calculate(rpn);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Object eval(Context context, String val) {
		try {
			Queue<Object> rpn = sy.parseToRPN(val);
			return rc.calculate(context, rpn);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	//@ JKTODO 添加变量支持
	//@ JKTODO 在变量基础上,添加数组支持
	//@ JKTODO abc(1+(1-1)),不知道这个函数请求会不会出错
}
