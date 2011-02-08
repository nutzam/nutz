package org.nutz.el;

import java.io.Reader;
import java.util.List;

import org.nutz.el.impl.NutElAnalyzer;
import org.nutz.el.impl.NutElValueMaker;
import org.nutz.el.impl.NutElSpliter;
import org.nutz.el.obj.*;
import org.nutz.el.val.*;
import org.nutz.lang.Lang;
import org.nutz.lang.util.Context;

public class El {

	private static ElSpliter spliter = new NutElSpliter();

	private static ElAnalyzer analyzer = new NutElAnalyzer();

	private static ElValueMaker valueMaker = new NutElValueMaker();

	public static List<ElSymbol> split(CharSequence cs) {
		return spliter.splite(Lang.inr(cs));
	}

	public static BinObj compile(CharSequence cs) {
		return compile(Lang.inr(cs));
	}

	public static BinObj compile(Reader reader) {
		List<ElSymbol> symbols = spliter.splite(reader);

		if (null == symbols || symbols.isEmpty())
			throw new ElException("Nothing for analyzing!");

		return analyzer.analyze(symbols.iterator());
	}

	public static ElValue eval(Context context, CharSequence cs) {
		BinObj en = compile(cs);
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
}
