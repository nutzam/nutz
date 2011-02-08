package org.nutz.el.impl;

import java.io.IOException;
import java.io.Reader;

import org.nutz.el.ElSymbol;

/**
 * 特殊符号的加载方式
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface SymbolLoader {

	/**
	 * 本实现是否接受从这个字符开始加载
	 * 
	 * @param prev
	 *            前一个符号
	 * @param c
	 *            测试字符
	 * 
	 * @return 是否可以开始加载
	 */
	boolean isMyTurn(ElSymbol prev, int c);

	/**
	 * @return 刚才生成的符号
	 */
	ElSymbol getSymbol();

	/**
	 * 从 Reader 中开始加载，这时，Reader 已经处于输出了测试字符的状态
	 * <p>
	 * 当加载器的实现类觉得，已经读取到了本符号的结尾，则必须从流中读取一个字符来返回
	 * <p>
	 * 主程序会根据这个字符来寻找新的加载器读取下一个符号
	 * <p>
	 * 比如，如果是字符串 'abc'+34 那么，处理符号 'abc' 的加载器需要返回符号 '+'，<br>
	 * 而处理符号 '+' 的加载器，则需返回字符 '3'
	 * 
	 * @param reader
	 *            文本流
	 * @return 本加载器最后一次从文本流中读取的字符。-1 则表示文本流已经结束了。
	 * @throws IOException
	 */
	int load(Reader reader) throws IOException;

}
