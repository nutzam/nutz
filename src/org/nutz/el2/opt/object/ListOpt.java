package org.nutz.el2.opt.object;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.nutz.el2.obj.IdentifierObj;
import org.nutz.el2.opt.TwoTernary;
import org.nutz.lang.Mirror;


/**
 * 列表对象,基本上用来做为一个分割符
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class ListOpt extends TwoTernary {

	public int fetchPriority() {
		return 1;
	}
	
	public void wrap(Queue<Object> rpn) {
		if(rpn.peek() instanceof AccessOpt){
			left = rpn.poll();
			return;
		}
		super.wrap(rpn);
	}
	
	@SuppressWarnings("unchecked")
	public Object calculate() {
		if(!(left instanceof AccessOpt)){
			//@ JKTODO 添加自定义方法的调用
			return null;
		}
		AccessOpt lval = (AccessOpt) left;
		Object[] objs = (Object[]) lval.calculate();
		Object obj = objs[0];
		if(objs[0] instanceof IdentifierObj){
			obj = ((IdentifierObj) objs[0]).fetchVal();
		}
		Object method = objs[1];
		
		List<Object> rvals = new ArrayList<Object>();
		if(right != null){
			if(right instanceof CommaOpt){
				rvals = (List<Object>) ((CommaOpt) right).calculate();
			} else {
				rvals.add(calculateItem(right));
			}
		}
		Mirror<?> me = Mirror.me(obj);
		if(rvals.isEmpty()){
			return me.invoke(obj, method.toString());
		}
		return me.invoke(obj, method.toString(), rvals.toArray());
	}
	public String fetchSelf() {
		return "list";
	}

}
