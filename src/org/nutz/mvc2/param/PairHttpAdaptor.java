package org.nutz.mvc2.param;

import org.nutz.mvc2.param.injector.NameInjector;

public class PairHttpAdaptor extends AbstractHttpAdaptor {

	@Override
	protected ParamInjector evalInjector(Class<?> type, String name) {
		return new NameInjector(name, type);
	}

}
