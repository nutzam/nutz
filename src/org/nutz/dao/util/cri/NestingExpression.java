package org.nutz.dao.util.cri;

import org.nutz.dao.Nesting;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.jdbc.ValueAdaptor;

/**
 * 与{@linkplain SimpleExpression}类似,但是
 * {@linkplain #joinSql(Entity, StringBuilder)},{@linkplain #joinParams(Entity, Object, Object[], int)}与{@linkplain #paramCount(Entity)}等返回值均为0.
 */
public class NestingExpression extends AbstractSqlExpression {

	private static final long serialVersionUID = 1L;

	private String op;
	private Nesting value;

	public NestingExpression(String name, String op, Nesting value) {
		super(name);
		this.op = op;
		this.value = value;
	}

	public void joinSql(Entity<?> en, StringBuilder sb) {
		if (!"EXISTS".equals(op))
			sb.append(_fmtcol(en));
		if (not)
			sb.append(" NOT");
		if ("=".equals(op) || ">".equals(op) || "<".equals(op) || "!=".equals(op)) {
			sb.append(op).append("(").append(value.toString()).append(")");
		} else {
			sb.append(" ").append(op).append(" ").append("(").append(value.toString()).append(")");
		}
	}

	public int joinAdaptor(Entity<?> en, ValueAdaptor[] adaptors, int off) {
		return 0;
	}

	public int joinParams(Entity<?> en, Object obj, Object[] params, int off) {
		return 0;
	}

	public int paramCount(Entity<?> en) {
		return 0;
	}

}
