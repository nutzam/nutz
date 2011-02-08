package org.nutz.el.opt;

import org.nutz.el.ElObj;
import org.nutz.el.ElValue;
import org.nutz.lang.util.Context;

public class InvokeOperator extends AbstractOperator {

	public InvokeOperator() {
		weight = 1000;
		str = "&invoke";
	}

	public ElValue execute(Context context, ElObj left, ElObj right) {
		ElValue l = left.eval(context);
		ElValue[] args = right.evalArray(context);
		return l.invoke(args);
	}

}
