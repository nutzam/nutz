package org.nutz.el.impl;

import java.util.Iterator;
import java.util.LinkedList;

import org.nutz.el.El;
import org.nutz.el.ElAnalyzer;
import org.nutz.el.ElException;
import org.nutz.el.ElObj;
import org.nutz.el.ElOperator;
import org.nutz.el.ElSymbol;
import org.nutz.el.ElSymbolType;
import org.nutz.el.obj.ArrayObj;
import org.nutz.el.obj.BinObj;
import org.nutz.el.obj.VarElObj;
import org.nutz.el.opt.InvokeOperator;

public class NutElAnalyzer implements ElAnalyzer {

	public BinObj analyze(Iterator<ElSymbol> it) {
		BinObj bin = new BinObj();

		ElSymbol symbol = it.next();

		/*
		 * 左值
		 */
		ElObj eo = evalElObj(bin, symbol, it);
		if (eo != null) {
			bin.setLeft(eo);
			symbol = it.hasNext() ? it.next() : null;
		}
		/*
		 * 操作符
		 */
		// 没有符号或者碰到 ')' 直接返回
		if (null == symbol || ElSymbolType.RIGHT_PARENTHESIS == symbol.getType()) {
			return root(bin);
		} else if (ElSymbolType.OPT == symbol.getType()) {
			bin.setOperator(symbol.getOperator());
			symbol = it.hasNext() ? it.next() : null;
		} else {
			throw new ElException("expect opertaor but it was <%s>", symbol.toString());
		}
		/*
		 * 右值
		 */
		// 没有符号或者碰到 ')' 直接返回
		if (null == symbol || ElSymbolType.RIGHT_PARENTHESIS == symbol.getType()) {
			return root(bin);
		}
		eo = evalElObj(bin, symbol, it);
		if (eo != null) {
			bin.setRight(eo);
		}

		/*
		 * 循环读取一个操作符，以及一个操作数，并依次增加到节点中
		 */
		while (it.hasNext()) {

			symbol = it.next();

			/*
			 * 如果碰到了 '('，则可能是一个调用，此时需要修改已经创建的节点
			 */
			if (symbol.getType() == ElSymbolType.LEFT_PARENTHESIS) {
				// 在 abc.func(1,2,3) 这种情况下，显然在遇到 '(' 时，node 已经有了右值了
				// 并且，操作符一定是 '.'，那么此时，需要循环读取迭代器，直到读取到 ')' 为止
				if (null != bin
					&& bin.getRight() != null
					&& (bin.getRight() instanceof VarElObj)
					&& bin.isAccessOperation()) {
					LinkedList<ElObj> objs = new LinkedList<ElObj>();
					// 先将右值当作字符串值压入堆栈
					objs.add(El.Obj.string(((VarElObj) bin.getRight()).getName()));
					// 循环读取
					while (it.hasNext()) {
						symbol = it.next();
						// 忽略逗号
						if (symbol.getType() == ElSymbolType.COMMA)
							continue;
						// 遇到 ')' 退出
						if (symbol.getType() == ElSymbolType.RIGHT_PARENTHESIS)
							break;
						// 生成 ElObj
						objs.add(evalElObj(null, symbol, it));
					}
					// 修改右式
					bin.setRight(new ArrayObj(objs.toArray(new ElObj[objs.size()])));
					// 修改操作符
					bin.setOperator(new InvokeOperator());

					// 重新循环
					continue;
				}
				// 肯定式语法错误啦
				else {
					throw new ElException("Wrong '(' after :\n%s", bin.toString());
				}
			}

			/*
			 * 如果不是函数调用，那么让我们来读取两个符号（一个操作符，一个数据对象）
			 */
			// 操作符
			ElOperator opt;
			// 碰到 ')' 直接返回
			if (ElSymbolType.RIGHT_PARENTHESIS == symbol.getType()) {
				return root(bin);
			}
			if (ElSymbolType.OPT == symbol.getType()) {
				opt = symbol.getOperator();
			} else {
				throw new ElException("expect opertaor but it was <%s>", symbol.toString());
			}

			// 检查是否还有符号
			if (!it.hasNext())
				throw new ElException("No more ElObject for operator '%s'!", opt.toString());

			// 操作数
			symbol = it.next();
			// 碰到 ')' 直接返回
			if (ElSymbolType.RIGHT_PARENTHESIS == symbol.getType()) {
				return root(bin);
			}
			ElObj obj = evalElObj(bin, symbol, it);

			// 添加到节点
			bin = bin.append(opt, obj);
		}

		// 寻找最顶层节点并返回
		return root(bin);
	}

	private ElObj evalElObj(BinObj bin, ElSymbol symbol, Iterator<ElSymbol> it) {
		switch (symbol.getType()) {
		case OPT:
			return null; // 操作符的话，节点不需要填充左值

		case BOOL:
			return El.Obj.oBoolean(symbol.getBoolean());

		case FLOAT:
			return El.Obj.oFloat(symbol.getFloat());

		case LONG:
			return El.Obj.oLong(symbol.getLong());

		case INT:
			return El.Obj.oInt(symbol.getInteger());

		case VAR:
			return El.Obj.var(symbol.getString());

		case STRING:
			return El.Obj.string(symbol.getString());

		case NULL:
			return El.Obj.oNull();

		case UNDEFINED:
			return El.Obj.undefined();

		case LEFT_PARENTHESIS:
			return analyze(it);

		case LEFT_BRACKET:
			break;

		case RIGHT_PARENTHESIS:
		case RIGHT_BRACKET:
		case COMMA:
			throw new ElException("Unexpect symbol : '%s'", symbol);
		}

		throw new ElException("Unknown symbol : '%s'", symbol);
	}

	private static BinObj root(BinObj node) {
		while (!node.isRoot())
			node = node.getParent();
		return node;
	}
}
