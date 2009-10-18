package org.nutz.mvc.param;

import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.param.injector.NameInjector;
import org.nutz.mvc.param.injector.ObjectPairInjector;

public class PairAdaptor extends AbstractAdaptor {

	@Override
	protected ParamInjector evalInjector(Class<?> type, Param param) {
		if (null == param)
			return null;
		if ("..".equals(param.value()))
			return new ObjectPairInjector(type);
		return new NameInjector(param.value(), type);
	}

}
