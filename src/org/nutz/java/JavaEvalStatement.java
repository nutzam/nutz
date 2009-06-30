package org.nutz.java;

import org.nutz.lang.Strings;

public class JavaEvalStatement extends JavaStatement {

	private String str;

	public JavaEvalStatement(String str) {
		if (null == str)
			this.str = ";";
		else {
			str = Strings.trim(str);
			if (str.endsWith(";"))
				this.str = str;
			else
				this.str = str + ";";
		}
	}

	@Override
	protected String renderSource() {
		return str;
	}

}
