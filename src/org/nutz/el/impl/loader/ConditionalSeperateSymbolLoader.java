package org.nutz.el.impl.loader;

import org.nutz.el.ElSymbol;
import org.nutz.el.ElSymbolType;

public class ConditionalSeperateSymbolLoader extends SpecialCharSymbolLoader {

	public ConditionalSeperateSymbolLoader() {
		super(':', (new ElSymbol()).setType(ElSymbolType.CONDITIONAL_SEP));
	}

}
