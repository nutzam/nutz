package org.nutz.el.impl.loader;

import org.nutz.el.ElSymbol;
import org.nutz.el.ElSymbolType;

public class ConditionalTestSymbolLoader extends SpecialCharSymbolLoader {

	public ConditionalTestSymbolLoader() {
		super('?', (new ElSymbol()).setType(ElSymbolType.CONDITIONAL_TEST));
	}

}
