package org.nutz.el2.opt.custom;

import org.nutz.el2.opt.object.RunMethod;

public class CustomMake {

	/**
	 * 自定义方法 工厂方法
	 * @param val
	 * @return
	 */
	public static RunMethod make(String val) {
		if("max".equals(val)){
			return new Max();
		}
		return null;
	}
	
}
