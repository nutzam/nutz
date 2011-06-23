package org.nutz.el2.opt.logic;

import org.nutz.el2.El2Exception;
import org.nutz.el2.opt.TwoTernary;

/**
 * or(||)
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class OrOpt extends TwoTernary{
	
	public int fetchPriority() {
		return 12;
	}
	public Object calculate() {
		Object lval = calculateItem(left);
		if(!(lval instanceof Boolean)){
			throw new El2Exception("操作数类型错误!");
		}
		if((Boolean)lval){
			return true;
		}
		Object rval = calculateItem(right);
		if(!(rval instanceof Boolean)){
			throw new El2Exception("操作数类型错误!");
		}
		if((Boolean)rval){
			return true;
		}
		return false;
	}
	public String fetchSelf() {
		return "||";
	}

}
