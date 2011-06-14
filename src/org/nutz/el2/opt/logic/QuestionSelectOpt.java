package org.nutz.el2.opt.logic;

import java.util.Queue;

import org.nutz.el2.opt.AbstractOpt;
import org.nutz.el2.opt.OptEnum;

/**
 * 三元运算符:
 * ':'
 * @ JKTODO 这里也读两个
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class QuestionSelectOpt extends AbstractOpt{
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
		if(!(left instanceof QuestionOpt)){
			throw new RuntimeException("三元表达式错误!");
		}
		QuestionOpt qo = (QuestionOpt) left;
		Boolean cval = (Boolean) qo.calculate();
		if(cval){
			return qo.getRight();
		}
		return right;
	}
	public OptEnum fetchSelf() {
		return OptEnum.QUESTION_SELECT;
	}

}
