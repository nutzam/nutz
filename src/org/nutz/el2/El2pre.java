package org.nutz.el2;

import java.util.LinkedList;
import java.util.Queue;

import org.nutz.el2.arithmetic.RPNCalculate;
import org.nutz.el2.arithmetic.ShuntingYard;
import org.nutz.lang.util.Context;

/**
 * 预编译
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class El2pre {
	private ShuntingYard sy = new ShuntingYard();
	private RPNCalculate rc = new RPNCalculate();
	private Queue<Object> rpn;
	
	public El2pre(String val) {
		rpn = sy.parseToRPN(val);
	}

	public Object eval(Context context) {
		Queue<Object> obj = new LinkedList<Object>();
		obj.addAll(rpn);
		return rc.calculate(context, obj);
	}
	
}
