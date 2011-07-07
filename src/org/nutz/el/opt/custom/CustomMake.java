package org.nutz.el.opt.custom;

import org.nutz.el.opt.RunMethod;

/**
 * 自定义函数工厂类
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class CustomMake {

	/**
	 * 自定义方法 工厂方法
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
