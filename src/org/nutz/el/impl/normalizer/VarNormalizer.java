package org.nutz.el.impl.normalizer;

import java.util.ArrayList;
import java.util.Iterator;

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
		if (ing.hasNext()) {
			// 一切根据下一个符号来决定
			ElSymbol next = ing.symbols[ing.index];

			/*
			 * 是访问操作符 - 那么会一直读取这个调用序列，直到碰到一个非 '.' 的操作符
			 */
			if (isAccessOperator(next)) {
				// 那么本符号一定是个变量，下面的符号则不好说，所以需要循环看看
				// 这个循环会生成一个 ElObj 组成的列表
				// 当然，列表项目只可能是 ArrayObj | VarElObj | BinElObj
				ArrayList<ElObj> list = new ArrayList<ElObj>();
				list.add(El.Obj.var(ing.current().getString()));
				// 跳过当前的 '.'
				ing.index++;
				// 开始循环
				while (ing.hasNext()) {
					next = ing.next();
					// 忽略调用操作符
					if (isAccessOperator(next))
						continue;
					// 变量名，表示属性，或者函数调用
					else if (ElSymbolType.VAR == next.getType()) {
						ElSymbol after = ing.symbols[ing.index];
						// 函数调用
						if (ElSymbolType.LEFT_PARENTHESIS == after.getType()) {
							list.add(readArgs(ing));
						}
						// 下标访问
						else if (ElSymbolType.LEFT_BRACKET == after.getType()) {
							list.add(El.Obj.var(next.getString()));
							// 移动到 '[' 处
							ing.index++;
							// 再次分析
							SymbolNormalizing newIng = ing.born();
							BinElObj obj = ing.analyzer.analyze(newIng);
							ing.index = newIng.index;
							// 确保遇到的是 ']'
							if (ElSymbolType.RIGHT_BRACKET != ing.current().getType()) {
								throw new ElException(	"'[' without close, nearby \"%s\"",
														ing.dumpError());
							}
							// 增加一个静态节点
							list.add(obj);
						}
						// 就是变量
						else {
							list.add(El.Obj.var(after.getString()));
						}
					}
					// 这个调用链结束了，一定是碰到了其它的操作符，所以要回退一步
					else {
						ing.index--;
						break;
					}
				}
				// 整理数组，形成一个 BinElObj
				Iterator<ElObj> it = list.iterator();
				BinElObj re = new BinElObj();
				re.setLeft(it.next());
				while (it.hasNext()) {
					ElObj obj = it.next();
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
					BinElObj nb = new BinElObj();
					nb.setLeft(re);
					re = nb;
				}
				return re.unwrap();
			}
			/*
			 * 是全局函数调用
			 */
			else if (ElSymbolType.LEFT_PARENTHESIS == next.getType()) {
				// 生成一个节点
				BinElObj re = new BinElObj();
				re.setLeft(new StaticElObj(new PojoElValue<Object>(El.global)));
				re.setOperator(InvokeOperator.me());

				// 读取参数表
				ArrayElObj argsObj = readArgs(ing);

				// 返回调用节点
				re.setRight(argsObj);
				return re;
			}
			/*
			 * 是下标访问 - 生成一个 Access 操作符的 BinObj 给它
			 */
			else if (ElSymbolType.LEFT_BRACKET == next.getType()) {
				// 生成一个节点
				BinElObj re = new BinElObj();
				re.setLeft(El.Obj.var(ing.current().getString()));
				// 移动到 '[' 处
				ing.index++;
				// 再次分析
				SymbolNormalizing newIng = ing.born();
				ElObj obj = ing.analyzer.analyze(newIng);
				ing.index = newIng.index;
				// 确保遇到的是 ']'
				if (ElSymbolType.RIGHT_BRACKET != ing.current().getType()) {
					throw new ElException("'[' without close, nearby \"%s\"", ing.dumpError());
				}
				// 返回一个属性访问节点
				re.setOperator(AccessOperator.me());
				re.setRight(obj);
				return re;
			}
		}
		// 就是一个变量
		return El.Obj.var(ing.current().getString());
	}

	public ArrayElObj readArgs(SymbolNormalizing ing) {
		ArrayList<ElObj> args = new ArrayList<ElObj>(5); // 很少有超过5个参数的函数吧
		// 先将右值当作字符串值压入堆栈
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
