package org.nutz.el2;

import java.util.Queue;

import org.nutz.el2.arithmetic.RPNCalculate;
import org.nutz.el2.arithmetic.ShuntingYard;
import org.nutz.lang.util.Context;

public class El2 {
	private RPNCalculate rc = null;
	
	public El2(){}
	/**
	 * 预编译
	 * @param cs
	 * @return
	 */
	public El2(CharSequence cs){
		ShuntingYard sy = new ShuntingYard();
		Queue<Object> rpn = sy.parseToRPN(cs.toString());
		rc = new RPNCalculate(rpn);
	}
	/**
	 * 解析预编译后的EL表达式
	 * @param context
	 * @return
	 */
	public Object eval(Context context) {
		if(rc == null){
			throw new El2Exception("没有进行预编译!");
		}
		return rc.calculate(context);
	}

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
		ShuntingYard sy = new ShuntingYard();
		RPNCalculate rc = new RPNCalculate();
		Queue<Object> rpn = sy.parseToRPN(val);
		return rc.calculate(context, rpn);
	}
	
	
	/**
	 * 说明:
	 * 1. 操作符优先级参考<Java运算符优先级参考图表>, 但不完全遵守,比如"()"
	 * 2. 使用Queue 的原因是,调用peek()方法不会读取串中的数据.
	 * 因为我希望达到的效果是,我只读取我需要的,我不需要的数据我不读出来.
	 */
	
}
