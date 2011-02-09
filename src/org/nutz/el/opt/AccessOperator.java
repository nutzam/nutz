package org.nutz.el.opt;

import org.nutz.el.El;
import org.nutz.el.ElObj;
import org.nutz.el.ElValue;
import org.nutz.el.ann.Opt;
import org.nutz.el.ann.Weight;
import org.nutz.lang.util.Context;

@Opt(".")
@Weight(100)
public class AccessOperator extends AbstractOperator {

	private static final AccessOperator me = El.opt(AccessOperator.class);

	public static AccessOperator me() {
		return me;
	}

	public ElValue execute(Context context, ElObj left, ElObj right) {
		ElValue l = left.eval(context);
		ElValue r = right.eval(context);
		return l.getProperty(r);
	}

}
