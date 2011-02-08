package org.nutz.el.impl.loader;

import org.nutz.el.ElSymbol;
import org.nutz.el.ElSymbolType;

public class RightPrenthesisSymbolLoader extends SpecialCharSymbolLoader {

	public RightPrenthesisSymbolLoader() {
		super(')', (new ElSymbol()).setType(ElSymbolType.RIGHT_PARENTHESIS));
	}

}
