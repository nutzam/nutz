package org.nutz.mvc.adaptor;

import org.nutz.lang.Mirror;
import org.nutz.mvc.adaptor.convertor.*;

public abstract class Params {

	public static ParamConvertor create(Class<?> type) {
		if (type.isArray())
			return new ArrayParamConvertor(type.getComponentType());

		Mirror<?> mirror = Mirror.me(type);
		if (mirror.isDateTimeLike()) {
			return new DateParamConvertor(type);
		}

		return new StringParamConvertor();
	}

}
