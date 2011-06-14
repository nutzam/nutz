package org.nutz.el2.opt;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.nutz.lang.Mirror;

/**
 * 执行
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class InvokeMethodOpt extends AbstractOpt {
	private Object left;
	private List<Object> right;

	@Override
	public int fetchPriority() {

		return 0;
	}

	@Override
	public void wrap(Queue<Object> operand) {
		right = new ArrayList<Object>();
		//读取参数列表
		while(!operand.isEmpty() && !(operand.peek() instanceof ListOpt)){
			right.add(0, operand.poll());
		}
		if(operand.peek() instanceof ListOpt){
			operand.poll();
		}
		
		left = operand.poll();
	}

	public Object calculate() {
		if(!(left instanceof AccessOpt)){
			//@ JKTODO 添加自定义方法的调用
			return null;
		}
		AccessOpt lval = (AccessOpt) left;
		Object[] objs = (Object[]) lval.calculate();
		Object obj = objs[0];
		Object method = objs[1];
		return Mirror.me(obj).invoke(obj, method.toString(), right.toArray());
	}

	public OptEnum fetchSelf() {
		return OptEnum.INVOKE;
	}

}
