package org.nutz.dao.util.cri;

import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;

public class Like extends NoParamsSqlExpression {

	static Like create(String name, String value, boolean ignoreCase) {
		Like like = new Like(name);
		like.value = value;
		like.ignoreCase = ignoreCase;
		like.left = "%";
		like.right = "%";
		return like;
	}

	private String value;

	private boolean ignoreCase;

	private String left;

	private String right;

	private Like(String name) {
		super(name);
	}

	public Like left(String left) {
		this.left = left;
		return this;
	}

	public Like right(String right) {
		this.right = right;
		return this;
	}

	public Like ignoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
		return this;
	}

	public void joinSql(Entity<?> en, StringBuilder sb) {
		String colName = _fmtcol(en);
		CharSequence colValue = Sqls.escapteConditionValue(value);
		if (not)
			sb.append(" NOT ");
		if (ignoreCase) {
			sb.append("LOWER(")
				.append(colName)
				.append(") LIKE LOWER('")
				.append(null == left ? "" : left)
				.append(colValue)
				.append(null == right ? "" : right)
				.append("')");
		} else {
			sb.append(colName)
				.append(" LIKE '")
				.append(null == left ? "" : left)
				.append(colValue)
				.append(null == right ? "" : right)
				.append("'");
		}
	}

}
