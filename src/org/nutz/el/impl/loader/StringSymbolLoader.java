package org.nutz.el.impl.loader;

import java.io.IOException;
import java.io.Reader;

import org.nutz.el.ElException;
import org.nutz.el.ElSymbol;
import org.nutz.el.ElSymbolType;
import org.nutz.lang.Lang;

public class StringSymbolLoader extends AbstractSymbolLoader {

	private StringBuilder sb;

	private int endBy;

	public boolean isMyTurn(ElSymbol prev, int c) {
		if (c == '"' || c == '\'') {
			endBy = c;
			sb = new StringBuilder();
			return true;
		}
		return false;
	}

	public int load(Reader reader) throws IOException {
		int last = -1;
		int c;
		while (-1 != (c = reader.read())) {
			// 处理逃逸字符
			if ('\\' == last) {
				switch (c) {
				case '\\':
				case 'r':
				case 'n':
				case 't':
				case '\'':
				case '"':
					sb.append((char) c);
					break;
				default:
					throw Lang.makeThrow("Unknown escape char '%c'", c);
				}
				continue;
			}
			// 遇到逃逸字符开始
			if ('\\' == c) {
				last = c;
				continue;
			}
			// 遇到字符串结束
			if (endBy == c) {
				symbol = new ElSymbol().setType(ElSymbolType.STRING).setObj(sb.toString());
				return reader.read();
			}
			// 默认增加到 buffer
			sb.append((char) c);
		}
		throw Lang.makeThrow(ElException.class, "String {%s} should be closed by '%c'", sb, endBy);
	}

}
