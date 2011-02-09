package org.nutz.el.opt;

import org.nutz.el.El;
import org.nutz.el.ElObj;
import org.nutz.el.ElValue;
import org.nutz.el.ann.*;
import org.nutz.lang.util.Context;

@Opt("&invoke")
@Weight(100)
@OptHidden
public class InvokeOperator extends AbstractOperator {

	private static final InvokeOperator me = El.opt(InvokeOperator.class);

	public static InvokeOperator me() {
		return me;
	}

	public InvokeOperator() {
		weight = 100;
		str = "&invoke";
	}

	public ElValue execute(Context context, ElObj left, ElObj right) {
		ElValue l = left.eval(context);
		ElValue[] args = right.evalArray(context);
		return l.invoke(args);
	}

}
