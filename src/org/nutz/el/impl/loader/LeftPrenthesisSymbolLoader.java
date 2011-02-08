package org.nutz.el.impl.loader;

import org.nutz.el.ElSymbol;
import org.nutz.el.ElSymbolType;

public class LeftPrenthesisSymbolLoader extends SpecialCharSymbolLoader {

	public LeftPrenthesisSymbolLoader() {
		super('(', (new ElSymbol()).setType(ElSymbolType.LEFT_PARENTHESIS));
	}

}
