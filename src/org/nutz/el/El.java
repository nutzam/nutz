package org.nutz.el;

import java.io.Reader;
import java.util.List;

import org.nutz.el.ann.Opt;
import org.nutz.el.ann.Weight;
import org.nutz.el.impl.NutElAnalyzer;
import org.nutz.el.impl.NutElGlobal;
import org.nutz.el.impl.NutElValueMaker;
import org.nutz.el.impl.NutElSpliter;
import org.nutz.el.impl.SymbolNormalizing;
import org.nutz.el.obj.*;
import org.nutz.el.val.*;
import org.nutz.lang.Lang;
import org.nutz.lang.util.Context;

/**
 * 表达式的帮助函数集
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class El {

	/**
	 * 拆分器类型
	 */
	public static Class<? extends ElSpliter> spliterType;

	/**
	 * 分析器 - 可以被用户改变，但必须式线程安全的
	 */
	public static ElAnalyzer analyzer = new NutElAnalyzer();

	/**
	 * 表达式值工厂 - 可以被用户改变，但必须式线程安全的
	 */
	public static ElValueMaker valueMaker = new NutElValueMaker();

	/**
	 * 表达式的全局函数集合。如果你想让你的表达式支持更多的全局函数，你可以改变这个对象
	 */
	public static Object global = new NutElGlobal();

	/**
	 * ElSpliter 并不假设是线程安全的，因为它可能会记录一下状态。因此每次都要通过这个函数来生成一个新的实例
	 * <p>
	 * 通过全局对象 El.spliterType 可以改变拆分器的类型
	 * 
	 * @return 符号拆分器
	 */
	public static ElSpliter spliter() {
		return new NutElSpliter();
	}

	/**
	 * 表达式预符号拆分
	 * 
	 * @param cs
	 *            输入文本
	 * @return 符号列表
	 */
	public static List<ElSymbol> split(CharSequence cs) {
		return spliter().splite(Lang.inr(cs));
	}

	/**
	 * 表达式预编译。会编译成一个二叉树，并返回根节点
	 * 
	 * @param cs
	 *            输入文本
	 * @return 表达式根节点
	 */
	public static BinElObj compile(CharSequence cs) {
		return compile(Lang.inr(cs));
	}

	/**
	 * 表达式预编译。会编译成一个二叉树，并返回根节点
	 * 
	 * @param cs
	 *            输入文本流
	 * @return 表达式根节点
	 */
	public static BinElObj compile(Reader reader) {
		List<ElSymbol> symbols = spliter().splite(reader);

		if (null == symbols || symbols.isEmpty())
			throw new ElException("Nothing for analyzing!");

		SymbolNormalizing ing = new SymbolNormalizing(	analyzer,
														symbols.toArray(new ElSymbol[symbols.size()]),
														0);

		return analyzer.analyze(ing);
	}

	/**
	 * 表达式计算
	 * 
	 * @param SimpleContext
	 *            上下文环境。 可以通过这个参数为表达式内的变量设值
	 * @param cs
	 *            输入文本
	 * @return 表达式的计算结果
	 */
	public static ElValue eval(Context context, CharSequence cs) {
		BinElObj en = compile(cs);
		return en.eval(context);
	}

	/**
	 * 表达式快速计算。不支持变量
	 * 
	 * @param cs
	 *            输入文本
	 * @return 表达式的计算结果
	 */
	public static ElValue eval(CharSequence cs) {
		return eval(Lang.context(), cs);
	}

	/**
	 * 将普通 Java 对象包裹成表达式的值对象
	 * 
	 * @param obj
	 *            普通 Java 对象
	 * @return 表达式值对象
	 */
	public static ElValue wrap(Object obj) {
		return valueMaker.make(obj);
	}

	/**
	 * 快速构建 ElObj 的方法
	 * 
	 * @author zozoh(zozohtnt@gmail.com)
	 */
	public static class Obj {

		public static ElObj string(String str) {
			return new StaticElObj(new StringElValue(str));
		}

		public static ElObj var(String name) {
			return new VarElObj(name);
		}

		public static ElObj oFloat(Float v) {
			return new StaticElObj(new FloatElValue(v));
		}

		public static ElObj oInt(Integer v) {
			return new StaticElObj(new IntegerElValue(v));
		}

		public static ElObj oLong(Long v) {
			return new StaticElObj(new LongElValue(v));
		}

		public static ElObj oBoolean(Boolean v) {
			return new StaticElObj(new BooleanElValue(v));
		}

		public static ElObj oNull() {
			return new StaticElObj(new NullElValue());
		}

		public static ElObj undefined() {
			return new StaticElObj(new UndefinedElValue());
		}

	}

	/**
	 * 快速生成操作符实例的帮助函数。
	 * <p>
	 * 它会根据表达式操作符的注解，为操作符设置显示值和权重
	 * 
	 * @param optType
	 *            操作符类型
	 * @return 操作符实例
	 */
	public static <T extends ElOperator> T opt(Class<T> optType) {
		try {
			T opt = (T) optType.newInstance();
			Opt optAnn = optType.getAnnotation(Opt.class);
			if (null != optAnn)
				opt.setString(optAnn.value());
			Weight weight = optType.getAnnotation(Weight.class);
			if (null != weight)
				opt.setWeight(weight.value());
			return opt;
		}
		catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
	}
}
