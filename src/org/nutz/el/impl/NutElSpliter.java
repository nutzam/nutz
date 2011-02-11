package org.nutz.el.impl;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.nutz.el.ElException;
import org.nutz.el.ElSpliter;
import org.nutz.el.ElSymbol;
import org.nutz.el.impl.loader.*;

/**
 * 这个实现右比较强的扩展性，我很满意
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class NutElSpliter implements ElSpliter {

	private List<SymbolLoader> loaders;

	public NutElSpliter() {
		loaders = new ArrayList<SymbolLoader>();
		
		loaders.add(new CommaSymbolLoader());
		loaders.add(new ConditionalSeperateSymbolLoader());
		loaders.add(new ConditionalTestSymbolLoader());
		loaders.add(new LeftBracketSymbolLoader());
		loaders.add(new LeftPrenthesisSymbolLoader());
		loaders.add(new RightBracketSymbolLoader());
		loaders.add(new RightPrenthesisSymbolLoader());
		loaders.add(new StringSymbolLoader());
		loaders.add(new NameSymbolLoader());
		loaders.add(new NumberLoader());
		loaders.add(new OptSymbolLoader());
	}

	public List<ElSymbol> splite(Reader reader) {
		try {
			// 首次读取一个非空测试字符，用来判断
			int c;
			while (-1 != (c = reader.read())) {
				if (!Character.isWhitespace(c))
					break;
			}
			// 准备返回对象
			List<ElSymbol> symbols = new LinkedList<ElSymbol>();
			ElSymbol lastSymbol = null;
			// 循环使用加载器
			while (c != -1) {
				// 寻找一个合适的加载器
				SymbolLoader sl = null;
				for (SymbolLoader l : loaders)
					if (l.isMyTurn(lastSymbol, c)) {
						sl = l;
						break;
					}
				// 没有找到加载器，抛出异常
				if (null == sl)
					throw new ElException("I don't know how to handle char '%c'", c);
				// 使用加载器加载
				c = sl.load(reader);
				lastSymbol = sl.getSymbol();
				symbols.add(lastSymbol);

				// 如果流结束
				if (-1 == c)
					break;

				// 保证这个字符不是空白字符
				while (Character.isWhitespace(c)) {
					c = reader.read();
					if (-1 == c)
						break;
				}

			}

			// 返回拆分结果
			return symbols;
		}
		catch (IOException e) {
			throw new ElException(e);
		}
	}

}
