package org.nutz.el.impl.loader;

import org.nutz.el.ElSymbol;
import org.nutz.el.impl.SymbolLoader;

public abstract class AbstractSymbolLoader implements SymbolLoader {

	protected ElSymbol symbol;

	public ElSymbol getSymbol() {
		return symbol;
	}

}