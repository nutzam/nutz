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

public class El {

	public static ElSpliter spliter = new NutElSpliter();

	public static ElAnalyzer analyzer = new NutElAnalyzer();

	public static ElValueMaker valueMaker = new NutElValueMaker();

	public static Object global = new NutElGlobal();

	public static List<ElSymbol> split(CharSequence cs) {
		return spliter.splite(Lang.inr(cs));
	}

	public static BinElObj compile(CharSequence cs) {
		return compile(Lang.inr(cs));
	}

	public static BinElObj compile(Reader reader) {
		List<ElSymbol> symbols = spliter.splite(reader);

		if (null == symbols || symbols.isEmpty())
			throw new ElException("Nothing for analyzing!");

		SymbolNormalizing ing = new SymbolNormalizing(	analyzer,
														symbols.toArray(new ElSymbol[symbols.size()]),
														0);

		return analyzer.analyze(ing);
	}

	public static ElValue eval(Context context, CharSequence cs) {
		BinElObj en = compile(cs);
		return en.eval(context);
	}

	public static ElValue eval(CharSequence cs) {
		return eval(new Context(), cs);
	}

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
