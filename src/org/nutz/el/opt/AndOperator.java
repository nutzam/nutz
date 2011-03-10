package org.nutz.el.opt;

import org.nutz.el.El;
import org.nutz.el.ElObj;
import org.nutz.el.ElValue;
import org.nutz.el.ann.Opt;
import org.nutz.el.ann.Weight;
import org.nutz.lang.util.Context;

@Opt("&&")
@Weight(value = 3, higherIfSame = true)
public class AndOperator extends AbstractOperator {

	public ElValue execute(Context context, ElObj left, ElObj right) {
		ElValue l = left.eval(context);
		if (l.getBoolean().booleanValue())
			return El.TRUE;
		ElValue r = right.eval(context);
		if (r.getBoolean().booleanValue())
			return El.TRUE;
		return El.FALSE;
	}

}
