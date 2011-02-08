package org.nutz.el.impl.loader;

import org.nutz.el.ElSymbol;
import org.nutz.el.ElSymbolType;

public class CommaSymbolLoader extends SpecialCharSymbolLoader {

	public CommaSymbolLoader() {
		super(',', (new ElSymbol()).setType(ElSymbolType.COMMA));
	}

}
