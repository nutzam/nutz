package org.nutz.el2.opt.custom;

import org.nutz.el2.opt.RunMethod;

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
		if("min".equals(val)){
			return new Min();
		}
		if("trim".equals(val)){
			return new Trim();
		}
		return null;
	}
	
}
