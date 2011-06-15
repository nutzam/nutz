package org.nutz.el2.opt.object;

import java.util.ArrayList;
import java.util.List;

import org.nutz.el2.opt.TwoTernary;

/**
 * ","
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class CommaOpt extends TwoTernary {
	public int fetchPriority() {
		return 0;
	}

	@SuppressWarnings("unchecked")
	public Object calculate() {
		List<Object> objs = new ArrayList<Object>();
		if(left instanceof CommaOpt){
			List<Object> tem = (List<Object>) ((CommaOpt) left).calculate();
			for(Object t : tem){
				objs.add(t);
			}
		}else{
			objs.add(calculateItem(left));
		}
		objs.add(calculateItem(right));
		return objs;
	}
	public String fetchSelf() {
		return ",";
	}

}
