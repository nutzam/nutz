package org.nutz.el2.opt.logic;

import org.nutz.el2.El2Exception;
import org.nutz.el2.opt.TwoTernary;

/**
 * 三元运算符:
 * ':'
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class QuestionSelectOpt extends TwoTernary{
	public int fetchPriority() {
		return 13;
	}
	public Object calculate() {
		if(!(left instanceof QuestionOpt)){
			throw new El2Exception("三元表达式错误!");
		}
		QuestionOpt qo = (QuestionOpt) left;
		Boolean cval = (Boolean) qo.calculate();
		if(cval){
			return qo.getRight();
		}
		return calculateItem(right);
	}
	public String fetchSelf() {
		return ":";
	}

}
