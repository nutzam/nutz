package org.nutz.el.impl.loader;

import java.io.IOException;
import java.io.Reader;

import org.nutz.el.ElSymbol;
import org.nutz.el.ElSymbolType;

public class NameSymbolLoader extends AbstractSymbolLoader {

	private StringBuilder sb;

	public boolean isMyTurn(ElSymbol prev, int c) {
		if ((c == '$' || c == '_') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
			sb = new StringBuilder();
			sb.append((char) c);
			return true;
		}
		return false;
	}

	public int load(Reader reader) throws IOException {
		int c;
		while (-1 != (c = reader.read())) {
			if ((c == '$' || c == '_')
				|| (c >= 'A' && c <= 'Z')
				|| (c >= 'a' && c <= 'z')
				|| (c >= '0' && c <= '9')) {
				sb.append((char) c);
			} else {
				break;
			}
		}
		String s = sb.toString();
		if (s.equals("true") || s.equals("false"))
			symbol = new ElSymbol().setType(ElSymbolType.BOOL).setObj(Boolean.valueOf(s));
		else if (s.equals("null"))
			symbol = new ElSymbol().setType(ElSymbolType.NULL);
		else if (s.equals("undefined"))
			symbol = new ElSymbol().setType(ElSymbolType.UNDEFINED);
		else
			symbol = new ElSymbol().setType(ElSymbolType.VAR).setObj(s);
		return c;
	}

}
