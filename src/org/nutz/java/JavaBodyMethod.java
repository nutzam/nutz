package org.nutz.java;

import java.util.ArrayList;
import java.util.List;

public class JavaBodyMethod extends JavaMethod {

	public JavaBodyMethod() {
		super();
		this.statements = new ArrayList<JavaStatement>();
	}

	private List<JavaStatement> statements;

	public JavaMethod addStatment(JavaStatement stat) {
		statements.add(stat);
		return this;
	}

	public List<JavaStatement> getStatements() {
		return statements;
	}

	@Override
	protected String renderSource() {
		StringBuilder sb = createMethodHead();
		sb.append(" {");
		for (JavaStatement stat : statements)
			sb.append("\n\t").append(Java.renderSource(stat));
		sb.append("\n}");
		return sb.toString();
	}
}
