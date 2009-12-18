package org.nutz.mvc.adaptor;

import org.nutz.mvc.adaptor.injector.ArrayInjector;
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
		// POJO
		if ("..".equals(pm)) {
			return new ObjectPairInjector(null, type);
		}
		// POJO with prefix
		else if (pm.startsWith("::") && pm.length() > 2) {
			return new ObjectPairInjector(pm.substring(2), type);
		}
		// POJO[]
		else if (type.isArray())
			return new ArrayInjector(pm, type);

		// Name-value
		return new NameInjector(pm, type);
	}

}
