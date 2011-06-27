package org.nutz.el2.obj;

import org.nutz.el2.arithmetic.ElCache;
import org.nutz.lang.util.Context;

/**
 * 标识符对象
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class IdentifierObj {
	private String val;
	private Context context;
	private ElCache ec;
	public IdentifierObj(String val) {
		this.val = val;
	}
	public String getVal() {
		return val;
	}
	public Object fetchVal(){
		context = ec.getContext();
		if(context != null && context.has(val)){
			return context.get(val);
		}
		return null;
	}
	public String toString() {
		return val;
	}
	public void setContext(Context context) {
		this.context = context;
	}
	public void setEc(ElCache ec) {
		this.ec = ec;
	}
}
