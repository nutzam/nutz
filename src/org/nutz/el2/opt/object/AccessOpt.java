package org.nutz.el2.opt.object;

import java.util.List;

import org.nutz.el2.Operator;
import org.nutz.el2.obj.IdentifierObj;
import org.nutz.el2.opt.TwoTernary;
import org.nutz.lang.Mirror;

/**
 * 访问符:'.'
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class AccessOpt extends TwoTernary implements RunMethod{
	public int fetchPriority() {
		return 1;
	}

	public Object calculate() {
		if(left instanceof AccessOpt){
			left = ((AccessOpt) left).fetchVar();
		}
		return new Object[]{left, right};
	}
	
	public Object run(List<Object> param) {
		Object obj = fetchVar();
		
		Mirror<?> me = Mirror.me(obj);
		if(param.isEmpty()){
			return me.invoke(obj, right.toString());
		}
		return me.invoke(obj, right.toString(), param.toArray());
	}
	
	/**
	 * 取得变得的值
	 * @return 
	 */
	public Object fetchVar(){
		if(left instanceof AccessOpt){
			return ((AccessOpt)left).fetchVar();
		}
		if(left instanceof Operator){
			return ((Operator) left).calculate();
		}
		if(left instanceof IdentifierObj){
			return ((IdentifierObj) left).fetchVal();
		}
		//@ JKTODO 添加属性读取
		//@ JKTODO 添加包引用
		return left;
	}

	public String fetchSelf() {
		return ".";
	}

}
