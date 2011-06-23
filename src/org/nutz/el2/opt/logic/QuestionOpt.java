package org.nutz.el2.opt.logic;

import org.nutz.el2.El2Exception;
import org.nutz.el2.Operator;
import org.nutz.el2.opt.TwoTernary;

/**
 * 三元运算符:
 * '?'
 * @author juqkai(juqkai@gmail.com)
 */
public class QuestionOpt extends TwoTernary{
	public int fetchPriority() {
		return 13;
	}
	public Object calculate() {
		if(left instanceof Operator){
			return ((Operator) left).calculate();
		}
		throw new El2Exception("三元表达式错误!");
	}

	public String fetchSelf() {
		return "?";
	}
}
