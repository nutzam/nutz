package org.nutz.el.opt.object;

import java.util.List;
import java.util.Map;

import org.nutz.el.Operator;
import org.nutz.el.opt.RunMethod;
import org.nutz.el.opt.TwoTernary;
import org.nutz.el.obj.IdentifierObj;
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
		//如果直接调用计算方法,那基本上就是直接调用属性了吧...我也不知道^^
		Object obj = fetchVar();
		if(obj instanceof Map){
			Map<?,?> om = (Map<?, ?>) obj;
			if(om.containsKey(right.toString())){
				return om.get(right.toString());
			}
		}
		
		Mirror<?> me = Mirror.me(obj);
		return me.getValue(obj, right.toString());
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
		return left;
	}

	public String fetchSelf() {
		return ".";
	}

}
