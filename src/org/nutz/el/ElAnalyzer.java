package org.nutz.el;

import org.nutz.el.impl.SymbolNormalizing;
import org.nutz.el.obj.BinElObj;

public interface ElAnalyzer {

	BinElObj analyze(SymbolNormalizing ing);

}
