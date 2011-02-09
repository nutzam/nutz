package org.nutz.el.impl.normalizer;

import org.nutz.el.El;
import org.nutz.el.ElObj;
import org.nutz.el.impl.SymbolNormalizer;
import org.nutz.el.impl.SymbolNormalizing;

public class BooleanNormalizer implements SymbolNormalizer {

	public ElObj normalize(SymbolNormalizing ing) {
		return El.Obj.oBoolean(ing.current().getBoolean());
	}

}
