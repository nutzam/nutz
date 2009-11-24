package org.nutz.dao.sql;

import org.nutz.dao.Condition;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.EntityField;

class PkCondition implements Condition {

	private Object[] args;

	PkCondition(Object[] args) {
		this.args = args;
	}

	public String toSql(Entity<?> entity) {
		StringBuilder sb = new StringBuilder();
		EntityField[] pks = entity.getPkFields();
		sb.append(pks[0].getColumnName()).append('=');
		sb.append(Sqls.formatFieldValue(args[0]));
		for (int i = 1; i < pks.length; i++) {
			sb.append(" AND ");
			sb.append(pks[i].getColumnName()).append('=');
			sb.append(Sqls.formatFieldValue(args[i]));
		}
		return sb.toString();
	}

}
