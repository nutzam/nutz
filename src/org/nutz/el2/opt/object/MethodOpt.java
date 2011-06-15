package org.nutz.el2.opt.object;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.nutz.el2.Operator;
import org.nutz.el2.opt.TwoTernary;
import org.nutz.el2.opt.custom.CustomMake;


/**
 * 方法体封装.
 * 主要是把方法的左括号做为边界
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class MethodOpt extends TwoTernary {

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
	
	public Object calculate(){
		RunMethod rm = fetchMethod();
		return rm.run(fetchParam());
	}
	
	private RunMethod fetchMethod(){
		if(!(left instanceof AccessOpt)){
			//@ JKTODO 添加自定义方法的调用
			return CustomMake.make(left.toString());
		}
		AccessOpt lval = (AccessOpt) left;
		return lval;
	}
	
//	public Object calculate() {
//		
//		Object[] objs = fetchMethodBody();
//		List<Object> rvals = fetchParam();
//		
//		Object obj = objs[0];
//		if(objs[0] instanceof IdentifierObj){
//			obj = ((IdentifierObj) objs[0]).fetchVal();
//		}
//		Object method = objs[1];
//		
//		
//		Mirror<?> me = Mirror.me(obj);
//		if(rvals.isEmpty()){
//			return me.invoke(obj, method.toString());
//		}
//		return me.invoke(obj, method.toString(), rvals.toArray());
//	}
//	
//	private Object[] fetchMethodBody(){
//		if(!(left instanceof AccessOpt)){
//			//@ JKTODO 添加自定义方法的调用
//			Custom cu = CustomMake.make(left.toString());
//			return cu.fetchMethodBody();
//		}
//		AccessOpt lval = (AccessOpt) left;
//		return (Object[]) lval.calculate();
//	}
	
	/**
	 * 取得方法执行的参数
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Object> fetchParam(){
		List<Object> rvals = new ArrayList<Object>();
		if(right != null){
			if(right instanceof CommaOpt){
				rvals = (List<Object>) ((CommaOpt) right).calculate();
			} else {
				rvals.add(calculateItem(right));
			}
		}
		if(!rvals.isEmpty()){
			for(int i = 0; i < rvals.size(); i ++){
				if(rvals.get(i) instanceof Operator){
					rvals.set(i, ((Operator)rvals.get(i)).calculate());
				}
			}
		}
		return rvals;
	}
	
	public String fetchSelf() {
		return "method";
	}

}
