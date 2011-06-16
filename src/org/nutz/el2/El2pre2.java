package org.nutz.el2;

import java.util.Queue;

import org.nutz.el2.arithmetic.RPNCalculate;
import org.nutz.el2.arithmetic.ShuntingYard;
import org.nutz.lang.util.Context;

/**
 * 预编译
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class El2pre2 {
	private ShuntingYard sy = new ShuntingYard();
	private RPNCalculate rc = new RPNCalculate();
	private Queue<Object> rpn;
	
	public El2pre2(String val) {
		rpn = sy.parseToRPN(val);
		rc = new RPNCalculate(rpn);
	}

	public Object eval(Context context) {
		return rc.calculate(context);
	}
	
}
