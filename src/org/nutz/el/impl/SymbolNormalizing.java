package org.nutz.el.impl;

import org.nutz.el.ElAnalyzer;
import org.nutz.el.ElSymbol;
import org.nutz.el.obj.BinElObj;

public class SymbolNormalizing {

	public SymbolNormalizing(ElAnalyzer analyzer, ElSymbol[] symbols, int off) {
		this.bin = new BinElObj();
		this.analyzer = analyzer;
		this.index = off;
		this.symbols = symbols;
	}

	public ElAnalyzer analyzer;

	public ElSymbol[] symbols;

	public BinElObj bin;

	public int index;

	public ElSymbol next() {
		return symbols[index++];
	}

	public ElSymbol current() {
		return symbols[index-1];
	}

	public boolean hasNext() {
		return hasNext(1);
	}

	public boolean hasNext(int num) {
		return (index + num) <= symbols.length;
	}

	public SymbolNormalizing born() {
		return new SymbolNormalizing(analyzer, symbols, index);
	}

	public String dumpError() {
		StringBuilder sb = new StringBuilder();
		sb.append("Unexcept symbol '").append(symbols[index]).append("'");
		sb.append(" nearby : ");
		for (int i = 0; i < index; i++) {
			sb.append(symbols[i]);
		}
		return sb.toString();
	}

	public BinElObj root() {
		while (!bin.isRoot()) {
			bin = bin.getParent();
		}
		return bin.unwrapToBin();
	}

}
