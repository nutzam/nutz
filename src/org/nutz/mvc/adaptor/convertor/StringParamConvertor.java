package org.nutz.mvc.adaptor.convertor;

import org.nutz.mvc.adaptor.ParamConvertor;

public class StringParamConvertor implements ParamConvertor {

	public Object convert(String[] ss) {
		if (null == ss || ss.length == 0)
			return null;
		//@ TODO 应该有数组转字符串的方法吧,我不知道...(juqkai,20110217) 
		String val = "";
		String temp = "";
		for(String s : ss){
			val += temp + s;
			temp = ",";
		}
		return val;
	}

}
