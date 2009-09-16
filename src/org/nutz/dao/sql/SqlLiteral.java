package org.nutz.dao.sql;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

/**
 * @author zozoh
 * 
 */
public class SqlLiteral {

	private void reset() {
		holders = new SimpleVarSet();
		vars = new SimpleVarSet();
		stack = new WorkingStack();
		varIndexes = new VarIndexes();
		holderIndexes = new VarIndexes();
	}

	private VarSet holders;
	private VarSet vars;
	private WorkingStack stack;
	private VarIndexes varIndexes;
	private VarIndexes holderIndexes;
	private String source;

	public VarSet holders() {
		return holders;
	}

	public VarSet vars() {
		return vars;
	}

	/**
	 * [@|$][a-zA-Z0-9_-.]+
	 * 
	 * <pre>
	 * 48-57	0-9
	 * 65-90	A-Z
	 * 97-122	a-z
	 * 95		_
	 * 45		-
	 * 46		.
	 * </pre>
	 * 
	 * @param str
	 * @return
	 */
	public SqlLiteral valueOf(String str) {
		reset();
		source = str;
		if (null == source)
			return this;
		char[] cs = Strings.trim(source).toCharArray();
		StringBuilder sb;
		for (int i = 0; i < cs.length; i++) {
			char c = cs[i];
			switch (c) {
			case '@':
				if (cs[i + 1] == '@') {
					stack.push(c);
					i++;
					break;
				}
				sb = new StringBuilder();
				i = readTokenName(cs, i, sb);
				// Fail to read token name
				if (sb.length() == 0) {
					stack.push(c);
				} else
					holderIndexes.add(sb.toString(), stack.markToken());
				break;
			case '$':
				if (cs[i + 1] == '$') {
					stack.push(c);
					i++;
					break;
				}
				sb = new StringBuilder();
				i = readTokenName(cs, i, sb);
				// Fail to read token name
				if (sb.length() == 0) {
					stack.push(c);
				} else
					varIndexes.add(sb.toString(), stack.markToken());
				break;
			default:
				stack.push(c);
			}
		}
		stack.finish();
		return this;
	}

	private int readTokenName(char[] cs, int i, StringBuilder sb) {
		for (++i; i < cs.length; i++) {
			int b = (int) cs[i];
			if (b == 95 || b == 45 || b == 46 || (b >= 48 && b <= 57) || (b >= 65 && b <= 90) || (b >= 97 && b <= 122))
				sb.append((char) b);
			else
				break;
		}
		return i - 1;
	}

	private void setValueByIndexes(String[] ss, VarIndexes indexes, String name, Object value) {
		String vs = null == value ? "" : value.toString();
		int[] is = indexes.get(name);
		if (null != is)
			for (int i : is)
				ss[stack.getIndex(i)] = vs;
	}

	public String toPrepareStatementString() {
		String[] ss = stack.cloneChain();
		// for vars
		for (String name : vars.keys())
			setValueByIndexes(ss, varIndexes, name, vars.get(name));

		// for holders
		for (String name : holders.keys())
			setValueByIndexes(ss, holderIndexes, name, "?");

		return Lang.concat(ss).toString();
	}

	public int[] getHolderIndexes(String name) {
		int[] is = this.holderIndexes.get(name);
		if (null != is)
			for (int i = 0; i < is.length; i++)
				is[i] += 1;
		return is;
	}

	@Override
	public SqlLiteral clone(){
		return new SqlLiteral().valueOf(source);
	}

	public String toString() {
		String[] ss = stack.cloneChain();
		// for vars
		for (String name : vars.keys())
			setValueByIndexes(ss, varIndexes, name, vars.get(name));

		// for holders
		for (String name : holders.keys())
			setValueByIndexes(ss, holderIndexes, name, holders.get(name));

		return Lang.concat(ss).toString();
	}

	public boolean isSELECT() {
		return stack.firstEquals("SELECT");
	}

	public boolean isUPDATE() {
		return stack.firstEquals("UPDATE");
	}

	public boolean isINSERT() {
		return stack.firstEquals("INSERT");
	}

	public boolean isDELETE() {
		return stack.firstEquals("DELETE");
	}

}
