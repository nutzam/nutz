package org.nutz.mvc.adaptor.convertor;

import org.nutz.mvc.adaptor.ParamConvertor;

public class StringParamConvertor implements ParamConvertor {

	public Object convert(String[] ss) {
		if (null == ss || ss.length == 0)
			return null;
		
		return ss[0];
	}

}
