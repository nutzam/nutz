package org.nutz.dao.sql;

import java.util.ArrayList;
import java.util.List;

import org.nutz.lang.Strings;
import org.nutz.lang.util.LinkedIntArray;

/**
 * It will record the date like
 * 
 * <pre>
 * chain: [str1][...][str2][...][...][str3]
 * indexes [1,3,4]   // 0 base indexes
 * </pre>
 */
class WorkingStack {

	WorkingStack() {
		sb = new StringBuilder();
		chain = new ArrayList<String>();
		indexes = new LinkedIntArray(20);
	}

	private String first;
	private StringBuilder sb;
	private List<String> chain;
	private LinkedIntArray indexes;

	void push(char c) {
		sb.append(c);
	}

	void finish() {
		if (sb.length() > 0)
			chain.add(sb.toString());
		if (chain.size() > 0) {
			first = chain.get(0);
			char[] cs = Strings.trim(first).toCharArray();
			int i = 0;
			for (; i < cs.length; i++) {
				char c = cs[i];
				if (c > 0 && c <= 32)
					break;
			}
			first = String.valueOf(cs, 0, i).toUpperCase();
		}
	}

	int markToken() {
		int re = indexes.size();
		if (sb.length() > 0) {
			chain.add(sb.toString());
			sb = new StringBuilder();
		}
		indexes.push(chain.size());
		chain.add("");
		return re;
	}

	String[] cloneChain() {
		return chain.toArray(new String[chain.size()]);
	}

	int getIndex(int i) {
		return indexes.get(i);
	}

	boolean firstEquals(String str) {
		if (null == first)
			return false;
		return first.equals(str);
	}

}
