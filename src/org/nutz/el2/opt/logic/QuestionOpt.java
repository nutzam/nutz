package org.nutz.el2.opt.logic;

import java.util.Queue;

import org.nutz.el2.Operator;
import org.nutz.el2.opt.AbstractOpt;
import org.nutz.el2.opt.OptEnum;

/**
 * 三元运算符:
 * '?'
 * @ JKTODO 这里读两个
 * @author juqkai(juqkai@gmail.com)
 * @ JKTODO 感觉这个可以不要了,直接使用 CommaOpt 类
 */
public class QuestionOpt extends AbstractOpt{
	private Object left;
	private Object right;

	public int fetchPriority() {
		return 13;
	}

	public void wrap(Queue<Object> operand) {
		right = operand.poll();
		left = operand.poll();
	}

	public Object calculate() {
		if(left instanceof Operator){
			return ((Operator) left).calculate();
		}
		throw new RuntimeException("三元表达式错误!");
	}

	public OptEnum fetchSelf() {
		return OptEnum.QUESTION;
	}

	public Object getRight() {
		if(right instanceof Operator){
			return ((Operator) right).calculate();
		}
		return right;
	}
}
