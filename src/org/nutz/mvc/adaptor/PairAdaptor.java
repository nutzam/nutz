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
		String pm = param.value();
		if ("..".equals(pm))
			return new ObjectPairInjector(null, type);
		else if (pm.startsWith("::") && pm.length() > 2)
			return new ObjectPairInjector(pm.substring(2), type);

		return new NameInjector(pm, type);
	}

}
