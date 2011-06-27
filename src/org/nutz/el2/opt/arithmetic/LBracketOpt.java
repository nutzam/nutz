package org.nutz.el2.opt.arithmetic;

import java.util.Queue;

import org.nutz.el2.El2Exception;
import org.nutz.el2.opt.AbstractOpt;

/**
 * "("
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class LBracketOpt extends AbstractOpt{
	public String fetchSelf() {
		return "(";
	}
	public int fetchPriority() {
		return 100;
	}
	
	public void wrap(Queue<Object> obj) {
		throw new El2Exception("'('符号不能进行wrap操作!");
	}
	public Object calculate() {
		throw new El2Exception("'('符号不能进行计算操作!");
	}
}
