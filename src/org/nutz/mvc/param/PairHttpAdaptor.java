package org.nutz.mvc.param;

import org.nutz.mvc.param.injector.NameInjector;

public class PairHttpAdaptor extends AbstractHttpAdaptor {

	@Override
	protected ParamInjector evalInjector(Class<?> type, String name) {
		return new NameInjector(name, type);
	}

}
