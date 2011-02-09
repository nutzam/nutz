package org.nutz.el.impl.normalizer;

import org.nutz.el.ElObj;
import org.nutz.el.impl.SymbolNormalizer;
import org.nutz.el.impl.SymbolNormalizing;

public class LeftParenthesisNormalizer implements SymbolNormalizer {

	public ElObj normalize(SymbolNormalizing ing) {
		// 创建新的运行时对象
		SymbolNormalizing newIng = ing.born();

		// 获得返回对象
		ElObj re = ing.analyzer.analyze(newIng);

		// 修改运行时对象偏移量
		ing.index = newIng.index;

		return re;
	}

}
