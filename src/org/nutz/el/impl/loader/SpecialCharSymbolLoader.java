package org.nutz.el.impl.loader;

import java.io.IOException;
import java.io.Reader;

import org.nutz.el.ElSymbol;

public abstract class SpecialCharSymbolLoader extends AbstractSymbolLoader {

	private int myChar;

	public SpecialCharSymbolLoader(char c, ElSymbol symbol) {
		this.myChar = c;
		this.symbol = symbol;
	}

	@Override
	public boolean isMyTurn(ElSymbol prev, int c) {
		return myChar == c;
	}

	@Override
	public int load(Reader reader) throws IOException {
		return reader.read();
	}

}
