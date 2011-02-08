package org.nutz.el.impl.loader;

import org.nutz.el.ElSymbol;
import org.nutz.el.ElSymbolType;

public class RightBracketSymbolLoader extends SpecialCharSymbolLoader {

	public RightBracketSymbolLoader() {
		super(']', (new ElSymbol()).setType(ElSymbolType.RIGHT_BRACKET));
	}

}
