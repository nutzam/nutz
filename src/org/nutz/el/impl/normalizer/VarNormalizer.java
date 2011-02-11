package org.nutz.el.impl.normalizer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import org.nutz.el.El;
import org.nutz.el.ElException;
import org.nutz.el.ElObj;
import org.nutz.el.ElSymbol;
import org.nutz.el.ElSymbolType;
import org.nutz.el.impl.SymbolNormalizer;
import org.nutz.el.impl.SymbolNormalizing;
import org.nutz.el.obj.ArrayElObj;
import org.nutz.el.obj.BinElObj;
import org.nutz.el.obj.StaticElObj;
import org.nutz.el.obj.VarElObj;
import org.nutz.el.opt.AccessOperator;
import org.nutz.el.opt.InvokeOperator;
import org.nutz.el.val.PojoElValue;

public class VarNormalizer implements SymbolNormalizer {

	public ElObj normalize(SymbolNormalizing ing) {
		/*
		 * 首先将后续符号，扫描至一个列表中，生成一个 List<ArrayObj|VarElObj|BinElObj>
		 */
		LinkedList<ElObj> stack = new LinkedList<ElObj>();
		BinElObj bin;

		// 首先为堆栈增加第一个元素，有可能是一个 VAR 也有可能是一个函数调用
		if (ing.hasNext() && ing.symbols[ing.index].getType() == ElSymbolType.LEFT_PARENTHESIS) {
			// 生成一个节点
			bin = new BinElObj();
			bin.setLeft(new StaticElObj(new PojoElValue<Object>(El.global)));
			bin.setOperator(InvokeOperator.me());
			bin.setRight(readArgs(ing));
			stack.add(bin);

		} else {
			stack.add(El.Obj.var(ing.current().getString()));
		}

		// 准备开始循环
		boolean breakLoop = false;
		ElObj obj;
		while (ing.hasNext()) {
			ElSymbol symbol = ing.next();
			switch (symbol.getType()) {
			// '[' 表示访问属性
			case LEFT_BRACKET:
				// 弹出前面的对象
				obj = stack.removeLast();
				// 生成一个节点
				bin = new BinElObj();
				bin.setLeft(obj);
				bin.setOperator(AccessOperator.me());

				// 分析属性值计算式
				SymbolNormalizing newIng = ing.born();
				BinElObj rightBin = ing.analyzer.analyze(newIng);
				ing.index = newIng.index;
				// 确保遇到的是 ']'
				if (ElSymbolType.RIGHT_BRACKET != ing.current().getType()) {
					throw new ElException("'[' without close, nearby \"%s\"", ing.dumpError());
				}

				// 确定右式，并存入堆栈
				bin.setRight(rightBin.unwrap());
				stack.add(bin);
				break;
			// 如果是变量，则压栈
			case VAR:
				// 如果后面是个调用 '(' 则将前面的对象弹出，当成调用的左式。
				if (ing.hasNext()
					&& ing.symbols[ing.index].getType() == ElSymbolType.LEFT_PARENTHESIS) {
					// 弹出前面的对象
					obj = stack.removeLast();
					// 生成一个节点
					bin = new BinElObj();
					bin.setLeft(obj);
					bin.setOperator(InvokeOperator.me());

					// 读取参数表
					obj = readArgs(ing);

					// 确定右式，并存入堆栈
					bin.setRight(obj);
					stack.add(bin);

					break;
				}
				// 否则同前面的 ElObj 构成访问节点
				else {
					obj = stack.removeLast();
					bin = new BinElObj();
					bin.setLeft(obj);
					bin.setOperator(AccessOperator.me());
					bin.setRight(El.Obj.var(symbol.getString()));
				}
				// 将调用表达式，压入堆栈保存
				stack.add(bin);
				break;
			// 操作符，是继续还是应该退出循环呢？
			case OPT:
				// 如果是 '.' 继续循环，当然，后面的符号如果不是 VAR，则抛错
				if (symbol.getOperator().is(".")) {
					if (!ing.hasNext() || ing.symbols[ing.index].getType() != ElSymbolType.VAR) {
						throw new ElException(ing.dumpError());
					}
					continue;
				}
				// 如果是其它操作符，则退出循环
				else {
					ing.index--; // 回退一下指针
					breakLoop = true;
				}
				break;
			// 遇到了 '?' 或者 ')' 或者 ':' 表示本调用链结束，需要回退指针，等待其它过程来处理
			case RIGHT_PARENTHESIS:
			case CONDITIONAL_SEP:
			case CONDITIONAL_TEST:
				ing.index--; // 回退一下指针
				breakLoop = true;
				break;
			// 默认认为遇到了不认识的符号，抛错
			default:
				throw new ElException(ing.dumpError());
			}
			if (breakLoop)
				break;
		}

		/*
		 * 分析列表，形成一个 BinElObj
		 */
		Iterator<ElObj> it = stack.iterator();
		BinElObj re = new BinElObj();
		re.setLeft(it.next());
		while (it.hasNext()) {
			obj = it.next();
			// 变量
			if (obj instanceof VarElObj) {
				re.setOperator(AccessOperator.me());
				re.setRight(obj);
			}
			// 数组
			else if (obj instanceof ArrayElObj) {
				re.setOperator(InvokeOperator.me());
				re.setRight(obj);
			}
			// 表达式
			else if (obj instanceof BinElObj) {
				re.setOperator(AccessOperator.me());
				re.setRight(((BinElObj) obj).unwrap());
			}
			// 灵异现象
			else {
				throw new ElException("Impossible!");
			}
			bin = new BinElObj();
			bin.setLeft(bin);
			re = bin;
		}

		/*
		 * 返回结果
		 */
		return re.unwrap();
	}

	public static ArrayElObj readArgs(SymbolNormalizing ing) {
		ArrayList<ElObj> args = new ArrayList<ElObj>(5); // 很少有超过5个参数的函数吧
		// 先将当前符号当作字符串值压入堆栈，它必定是一个 VAR
		args.add(El.Obj.string(ing.current().getString()));

		// 移动到 '('
		ing.index++;

		// 逐次增加对象
		while (ing.hasNext() && ing.current().getType() != ElSymbolType.RIGHT_PARENTHESIS) {
			SymbolNormalizing newIng = ing.born();
			BinElObj arg = ing.analyzer.analyze(newIng);
			ing.index = newIng.index;
			if (arg == null)
				break;
			args.add(arg.unwrap());
		}
		ArrayElObj argsObj = new ArrayElObj(args.toArray(new ElObj[args.size()]));
		return argsObj;
	}

	public boolean isAccessOperator(ElSymbol next) {
		return ElSymbolType.OPT == next.getType() && (next.getOperator() instanceof AccessOperator);
	}

}
