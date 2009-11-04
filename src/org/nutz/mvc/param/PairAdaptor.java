package org.nutz.mvc.param;

import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.param.injector.NameInjector;
import org.nutz.mvc.param.injector.ObjectPairInjector;
import org.nutz.mvc.param.injector.PathArgInjector;

public class PairAdaptor extends AbstractAdaptor {

	@Override
	protected ParamInjector evalInjector(Class<?> type, Param param) {
		if (null == param)
			return new PathArgInjector(type);
		if ("..".equals(param.value()))
			return new ObjectPairInjector(type);
		return new NameInjector(param.value(), type);
	}

}
