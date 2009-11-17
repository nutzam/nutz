package org.nutz.mvc.adaptor;

import org.nutz.mvc.adaptor.injector.NameInjector;
import org.nutz.mvc.adaptor.injector.ObjectPairInjector;
import org.nutz.mvc.adaptor.injector.PathArgInjector;
import org.nutz.mvc.annotation.Param;

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
