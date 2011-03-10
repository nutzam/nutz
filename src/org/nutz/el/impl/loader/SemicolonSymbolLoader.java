package org.nutz.el.impl.loader;

import org.nutz.el.ElSymbol;
import org.nutz.el.ElSymbolType;

public class SemicolonSymbolLoader extends SpecialCharSymbolLoader {

	public SemicolonSymbolLoader() {
		super(';', (new ElSymbol()).setType(ElSymbolType.SEMICOLON));
	}

}
