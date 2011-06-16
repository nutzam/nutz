package org.nutz.el2.opt.arithmetic;

import org.nutz.el2.opt.TwoTernary;

/**
 * "+"
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class PlusOpt extends TwoTernary {
	public int fetchPriority() {
		return 4;
	}

	public String fetchSelf() {
		return "+";
	}
	public Object calculate() {
		if(this.right instanceof String || this.left instanceof String){
			return this.left.toString() + this.right.toString();
		}
		
		Number lval = (Number) calculateItem(this.left);
		Number rval = (Number) calculateItem(this.right);
		if(rval instanceof Double || lval instanceof Double){
			return lval.doubleValue() + rval.doubleValue();
		}
		if(rval instanceof Float || lval instanceof Float){
			return lval.floatValue() + rval.floatValue();
		}
		if(rval instanceof Long || lval instanceof Long){
			return lval.longValue() + rval.longValue();
		}
		return lval.intValue() + rval.intValue();
	}

}
