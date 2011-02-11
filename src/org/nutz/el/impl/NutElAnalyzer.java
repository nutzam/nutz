package org.nutz.el.impl;

import java.util.HashMap;
import java.util.Map;

import org.nutz.el.ElAnalyzer;
import org.nutz.el.ElException;
import org.nutz.el.ElObj;
import org.nutz.el.ElOperator;
import org.nutz.el.ElSymbol;
import org.nutz.el.ElSymbolType;
import org.nutz.el.impl.normalizer.*;
import org.nutz.el.obj.ArrayElObj;
import org.nutz.el.obj.BinElObj;
import org.nutz.el.obj.ConditionalElObj;
import org.nutz.el.opt.InvokeOperator;

public class NutElAnalyzer implements ElAnalyzer {

	private Map<ElSymbolType, SymbolNormalizer> map;

	public NutElAnalyzer() {
		map = new HashMap<ElSymbolType, SymbolNormalizer>();
		map.put(ElSymbolType.BOOL, new BooleanNormalizer());
		map.put(ElSymbolType.FLOAT, new FloatNormalizer());
		map.put(ElSymbolType.INT, new IntNormalizer());
		map.put(ElSymbolType.LONG, new LongNormalizer());
		map.put(ElSymbolType.NULL, new NullNormalizer());
		map.put(ElSymbolType.STRING, new StringNormalizer());
		map.put(ElSymbolType.UNDEFINED, new UndefinedNormalizer());
		map.put(ElSymbolType.LEFT_PARENTHESIS, new LeftParenthesisNormalizer());
		map.put(ElSymbolType.VAR, new VarNormalizer());
	}

	public BinElObj analyze(SymbolNormalizing ing) {
		// ----------------------------------------------------[检查结束]
		// 如果没有符号了，直接返回
		if (!ing.hasNext())
			return null;
		// ---------------------------------------------------[检查结束符]
		// 判断表达式是否是第一个符号就结束
		ElSymbol sy = ing.next();
		if (isEnd(sy)) {
			return null;
		}

		// ------------------------------------------------------[左值]
		SymbolNormalizer syor;
		ElObj obj;
		// 首先初始化，填充二叉树节点的左值
		if (ElSymbolType.OPT != sy.getType()) {
			// 寻找一个整理器
			syor = map.get(sy.getType());
			// 没有发现合适的整理器，抛错
			if (null == syor) {
				throw new ElException(ing.dumpError());
			}
			obj = syor.normalize(ing);
			ing.bin.setLeft(obj);
		}

		// ------------------------------------------------------[循环]
		// 之后，操作符同符号应该成对出现，一定是一个操作符，之后是一个对象
		ElOperator opt;
		while (ing.hasNext()) {
			// 判断表达式是否结束
			sy = ing.next();
			if (isEnd(sy)) {
				break;
			}

			// 获取操作符
			if (ElSymbolType.OPT == sy.getType()) {
				// 如果是个调用符号
				if (sy.is(".")) {
					sy = ing.next();
					// 后一个对象必须是 VAR
					if (ElSymbolType.VAR != sy.getType()) {
						throw new ElException(ing.dumpError());
					}
					// 获取被调用对象
					ElObj invoked = ing.bin.isLeftOnly() ? ing.bin.getLeft() : ing.bin;
					ArrayElObj args = VarNormalizer.readArgs(ing);
					// 替换表达式节点
					ing.bin = new BinElObj();
					ing.bin.setLeft(invoked);
					ing.bin.setOperator(InvokeOperator.me());
					ing.bin.setRight(args);
					continue;
				}
				// 普通操作符
				else {
					opt = sy.getOperator();
				}
			}
			// 是个条件判断符，将前面的表达式作为判断表达式，然后连续读取左值和右值
			else if (ElSymbolType.CONDITIONAL_TEST == sy.getType()) {
				putConditionalAsLeft(ing);
				continue;
			}
			// 不晓得是虾米，抛错者也~~
			else {
				throw new ElException(ing.dumpError());
			}

			// 检查
			if (!ing.hasNext())
				throw new ElException("Lack ElObj in the end!");

			// 获取对象
			sy = ing.next();
			syor = map.get(sy.getType());
			obj = syor.normalize(ing);

			ing.bin = ing.bin.append(opt, obj);
		}

		// 全部操作完成，返回节点
		return ing.root();
	}

	private static void putConditionalAsLeft(SymbolNormalizing ing) {
		// 获取 trueObj
		SymbolNormalizing newIng = ing.born();
		ElObj trueObj = ing.analyzer.analyze(newIng).unwrap();
		ing.index = newIng.index;

		// 判断一下这时候是不是 ':'
		if (!ing.current().is(":"))
			throw new ElException(ing.dumpError());
		// 获取 falseObj
		newIng = ing.born();
		ElObj falseObj = ing.analyzer.analyze(newIng).unwrap();
		ing.index = newIng.index;

		// 肯定是被 ']' | ')' | ',' 结束的。不用回退
		if (ing.hasNext() && ing.current().is(":"))
			throw new ElException(ing.dumpError());

		// 创建条件表达式对象
		ConditionalElObj cnd = new ConditionalElObj();

		// 看看现在的表达式是否被填满了
		if (!ing.bin.isLeftOnly()) {
			BinElObj newBin = new BinElObj();
			newBin.setLeft(ing.bin);
			ing.bin = newBin;
		}
		// 替换现有节点左值
		cnd.setTest(ing.bin.getLeft());
		cnd.setTrueObj(trueObj);
		cnd.setFalseObj(falseObj);
		ing.bin.setLeft(cnd);
	}

	private static boolean isEnd(ElSymbol sy) {
		switch (sy.getType()) {
		case RIGHT_PARENTHESIS:
		case RIGHT_BRACKET:
		case COMMA:
		case CONDITIONAL_SEP:
			return true;
		}
		return false;
	}

}
