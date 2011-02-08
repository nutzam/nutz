package org.nutz.el.obj;

import org.nutz.el.ElObj;
import org.nutz.el.ElValue;
import org.nutz.lang.Lang;
import org.nutz.lang.util.Context;

public class StaticElObj implements ElObj {

	private ElValue val;

	public StaticElObj(ElValue val) {
		this.val = val;
	}

	public ElValue eval(Context context) {
		return val;
	}

	public ElValue[] evalArray(Context context) {
		throw Lang.noImplement();
	}
	
	public String toString(){
		return val.toString();
	}

}
