package org.nutz.el.impl.loader;

import java.io.IOException;
import java.io.Reader;

import org.nutz.el.ElSymbol;
import org.nutz.el.ElSymbolType;
import org.nutz.lang.Lang;

public class NumberLoader extends AbstractSymbolLoader {

	private StringBuilder sb;

	public boolean isMyTurn(ElSymbol prev, int c) {
		// 对于 '.'，之前的符号如果是 ',' | '(' | '[' 则都是数字
		if (c == '.') {
			if (null != prev) {
				ElSymbolType prevType = prev.getType();
				if (prevType != ElSymbolType.COMMA
					&& prevType != ElSymbolType.OPT
					&& prevType != ElSymbolType.LEFT_PARENTHESIS
					&& prevType != ElSymbolType.LEFT_BRACKET)
					return false;
			}
		}
		// 数字开头的均接受
		else if (c < '0' || c > '9') {
			return false;
		}
		sb = new StringBuilder();
		sb.append((char) c);
		return true;
	}

	@Override
	public int load(Reader reader) throws IOException {
		int c;
		// 确保
		while (-1 != (c = reader.read())) {
			if (c == '.'
				|| (c >= '0' && c <= '9')
				|| c == 'f'
				|| c == 'F'
				|| c == 'l'
				|| c == 'L'
				|| c == 'x'
				|| c == 'X') {
				sb.append((char) c);
			} else {
				break;
			}
		}

		Number num = Lang.str2number(sb.toString());
		symbol = new ElSymbol().setObj(num);

		if (num instanceof Integer)
			symbol.setType(ElSymbolType.INT);
		else if (num instanceof Long)
			symbol.setType(ElSymbolType.LONG);
		else if (num instanceof Float)
			symbol.setType(ElSymbolType.FLOAT);

		return c;
	}

}
