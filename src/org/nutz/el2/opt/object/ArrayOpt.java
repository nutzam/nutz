package org.nutz.el2.opt.object;

import java.lang.reflect.Array;

import org.nutz.el2.opt.TwoTernary;

public class ArrayOpt extends TwoTernary {
	public int fetchPriority() {
		return 1;
	}
	public Object calculate() {
		Object lval = calculateItem(left);
		Object rval = calculateItem(right);
		return Array.get(lval, (Integer)rval);
	}
	public String fetchSelf() {
		return "]";
	}
}
