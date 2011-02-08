package org.nutz.el.obj;

import org.nutz.el.El;
import org.nutz.el.ElObj;
import org.nutz.el.ElValue;
import org.nutz.lang.Lang;
import org.nutz.lang.util.Context;

public class VarElObj implements ElObj {

	private String name;

	public VarElObj(String name) {
		this.name = name;
	}

	public ElValue eval(Context context) {
		return El.wrap(context.get(name));
	}

	public ElValue[] evalArray(Context context) {
		throw Lang.noImplement();
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return "$" + name;
	}

}
