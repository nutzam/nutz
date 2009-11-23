package org.nutz.dao.tools.impl;

import java.util.Iterator;

import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.tools.DField;
import org.nutz.dao.tools.DTable;
import org.nutz.dao.tools.TableDefinition;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.CharSegment;

public class TableDefinitionImpl implements TableDefinition {

	private SqlExpert expert;

	public TableDefinitionImpl(SqlExpert expert) {
		this.expert = expert;
	}

	private static void setIndexes(CharSegment seg, String key, String value) {
		if (!Strings.isBlank(value))
			value = "\n," + value;
		seg.set(key, value);
	}

	public Sql makeCreateSql(DTable dt) {
		// 数据表必须有字段
		if (dt.getFields().isEmpty())
			throw Lang.makeThrow("Table '%s' should include at least one field!", dt.getName());
		// 复合主键，不允许自增
		if (dt.getPks().size() > 1)
			for (DField pk : dt.getPks()) {
				if (pk.isAutoIncreament())
					throw Lang.makeThrow(
							"Table '%s'.'%s' should be auto-increase, because, it is multi-PK", dt
									.getName(), pk.getName());
			}

		// 生成字段定义
		Iterator<DField> it = dt.getFields().iterator();
		StringBuilder sb = new StringBuilder();
		sb.append(expert.tellField(0, it.next()));
		while (it.hasNext()) {
			sb.append("\n,").append(expert.tellField(dt.getPks().size(), it.next()));
		}
		// 生成 SQL
		CharSegment seg = new CharSegment(expert.tellCreateSqlPattern());
		seg.set("table", dt.getName());
		seg.set("fields", sb);
		setIndexes(seg, "pks", expert.tellPKs(dt));
		Sql sql = Sqls.create(seg.toString());
		// 将 SQL 对象处理成数据库方言，并返回
		return expert.evalCreateSql(dt, sql);
	}

	public Sql makeDropSql(DTable dt) {
		Sql drop = Sqls.create(String.format("DROP TABLE %s", dt.getName()));
		return expert.evalDropSql(dt, drop);
	}

}
