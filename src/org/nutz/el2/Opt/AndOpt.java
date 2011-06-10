package org.nutz.el2.Opt;

import java.util.Queue;

public class AndOpt extends AbstractOpt {
	public int fetchPriority() {
		return 6;
	}

	public Object calculate(Queue<Object> operand) {
		Object right = operand.poll();
		Object left = operand.poll();
		if(right instanceof Boolean && left instanceof Boolean){
			return (Boolean)left && (Boolean) right;
		}
		throw new RuntimeException("操作数类型错误!");
	}

	public OptEnum fetchSelf() {
		return OptEnum.AND;
	}

}
