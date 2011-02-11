package org.nutz.el.obj;

import org.nutz.el.ElObj;
import org.nutz.el.ElValue;
import org.nutz.lang.Lang;
import org.nutz.lang.util.Context;

/**
 * 静态表达式对象
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class StaticElObj implements ElObj {

	private ElValue obj;

	public StaticElObj(ElValue val) {
		this.obj = val;
	}

	public ElValue eval(Context context) {
		return obj;
	}

	public ElValue[] evalArray(Context context) {
		throw Lang.noImplement();
	}

	public ElValue getObj() {
		return obj;
	}

	public void setObj(ElValue val) {
		this.obj = val;
	}

	public String toString() {
		return obj.toString();
	}

}
