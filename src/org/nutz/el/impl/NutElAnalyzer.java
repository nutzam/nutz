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
import org.nutz.el.obj.BinElObj;

public class NutElAnalyzer implements ElAnalyzer {

	public BinElObj analyze(SymbolNormalizing ing) {
		// ----------------------------------------------------[检查结束]
		// 如果没有符号了，直接返回
		if (!ing.hasNext())
			return null;
		// ---------------------------------------------------[检查右括号]
		// 如果遇到右括号和逗号，也结束
		ElSymbol sy = ing.next();
		if (ElSymbolType.RIGHT_PARENTHESIS == sy.getType()
			|| ElSymbolType.RIGHT_BRACKET == sy.getType()
			|| ElSymbolType.COMMA == sy.getType()) {
			return null;
		}
		SymbolNormalizer syor;
		// ------------------------------------------------------[左值]
		// 首先初始化，填充二叉树节点的左值
		if (ElSymbolType.OPT != sy.getType()) {
			// 寻找一个整理器
			syor = map.get(sy.getType());
			// 没有发现合适的整理器，抛错
			if (null == syor) {
				throw new ElException(ing.dumpError());
			}
			ing.bin.setLeft(syor.normalize(ing));
		}
		// ----------------------------------------------------[检查结束]
		// 如果没有符号了，直接返回
		if (!ing.hasNext())
			return ing.root();
		// ---------------------------------------------------[检查右括号]
		// 如果遇到右括号和逗号，也结束
		sy = ing.next();
		if (ElSymbolType.RIGHT_PARENTHESIS == sy.getType()
			|| ElSymbolType.RIGHT_BRACKET == sy.getType()
			|| ElSymbolType.COMMA == sy.getType()) {
			return ing.root();
		}
		// -----------------------------------------------------[操作符]
		// 二叉树节点的操作符在此时必须有
		if (ElSymbolType.OPT == sy.getType()) {
			ing.bin.setOperator(sy.getOperator());
		}
		// 靠，神马情况？ 抛个错先
		else {
			throw new ElException("Expect opertaor but it was \"%s\"", sy.toString());
		}
		// ----------------------------------------------------[检查结束]
		// 如果没有符号了，直接返回
		if (!ing.hasNext())
			return ing.root();
		// ---------------------------------------------------[检查右括号]
		// 如果遇到右括号和逗号，也结束
		sy = ing.next();
		if (ElSymbolType.RIGHT_PARENTHESIS == sy.getType()
			|| ElSymbolType.RIGHT_BRACKET == sy.getType()
			|| ElSymbolType.COMMA == sy.getType()) {
			return ing.root();
		}
		// ------------------------------------------------------[右值]
		// 寻找一个整理器
		syor = map.get(sy.getType());
		// 没有发现合适的整理器，抛错
		if (null == syor) {
			throw new ElException(ing.dumpError());
		}
		// 设置右值
		ing.bin.setRight(syor.normalize(ing));

		// ------------------------------------------------------[循环]
		// 之后，操作符同符号应该成对出现，一定是一个操作符，之后是一个对象
		ElOperator opt;
		ElObj obj;
		while (ing.hasNext()) {
			// 如果遇到右括号和逗号，也结束
			sy = ing.next();
			if (ElSymbolType.RIGHT_PARENTHESIS == sy.getType()
				|| ElSymbolType.RIGHT_BRACKET == sy.getType()
				|| ElSymbolType.COMMA == sy.getType()) {
				break;
			}

			// 获取操作符
			if (ElSymbolType.OPT != sy.getType()) {
				throw new ElException("Expect opertaor but it was \"%s\"", sy.toString());
			}
			opt = sy.getOperator();

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
}
