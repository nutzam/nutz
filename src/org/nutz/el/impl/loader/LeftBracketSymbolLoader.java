package org.nutz.el.impl.loader;

import org.nutz.el.ElSymbol;
import org.nutz.el.ElSymbolType;

public class LeftBracketSymbolLoader extends SpecialCharSymbolLoader {

	public LeftBracketSymbolLoader() {
		super('[', (new ElSymbol()).setType(ElSymbolType.LEFT_BRACKET));
	}

}
