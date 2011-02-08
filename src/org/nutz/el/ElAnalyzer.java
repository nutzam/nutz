package org.nutz.el;

import java.util.Iterator;

import org.nutz.el.obj.BinObj;

public interface ElAnalyzer {

	BinObj analyze(Iterator<ElSymbol> it);

}
