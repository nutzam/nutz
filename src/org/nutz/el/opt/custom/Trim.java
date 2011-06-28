package org.nutz.el.opt.custom;

import java.util.List;

import org.nutz.el.ElException;
import org.nutz.el.opt.RunMethod;

public class Trim implements RunMethod{
	public Object run(List<Object> fetchParam) {
		if(fetchParam.size() <= 0){
			throw new ElException("trim方法参数错误");
		}
		String obj = (String) fetchParam.get(0);
		return obj.trim();
	}

}
