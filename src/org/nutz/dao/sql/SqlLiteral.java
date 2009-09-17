package org.nutz.dao.sql;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

/**
 * @author zozoh
 * 
 */
public class SqlLiteral {

	private void reset() {
		params = new SimpleVarSet();
		vars = new SimpleVarSet();
		stack = new WorkingStack();
		varIndexes = new VarIndexes();
		paramIndexes = new VarIndexes();
		statementIndexes = new VarIndexes();
	}

	private VarSet params;
	private VarSet vars;
	private WorkingStack stack;
	private VarIndexes varIndexes;
	private VarIndexes paramIndexes;
	private VarIndexes statementIndexes;
	private String source;

	public VarSet getParams() {
		return params;
	}

	public void setParams(VarSet holders) {
		this.params = holders;
	}

	public VarSet getVars() {
		return vars;
	}

	public void setVars(VarSet vars) {
		this.vars = vars;
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
		int statementIndex = 1;
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
				} else {
					String name = sb.toString();
					paramIndexes.add(name, stack.markToken());
					statementIndexes.add(name, statementIndex++);
				}
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
		for (String name : params.keys())
			setValueByIndexes(ss, paramIndexes, name, "?");

		return Lang.concat(ss).toString();
	}

	public int[] getParamIndexes(String name) {
		return statementIndexes.get(name);
	}

	@Override
	public SqlLiteral clone() {
		return new SqlLiteral().valueOf(source);
	}

	public String toString() {
		String[] ss = stack.cloneChain();
		// for vars
		for (String name : vars.keys())
			setValueByIndexes(ss, varIndexes, name, vars.get(name));

		// for holders
		for (String name : params.keys())
			setValueByIndexes(ss, paramIndexes, name, params.get(name));

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

	public boolean isCREATE() {
		return stack.firstEquals("CREATE");
	}

	public boolean isDROP() {
		return stack.firstEquals("DROP");
	}
}
