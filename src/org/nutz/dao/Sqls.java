package org.nutz.dao;

import java.util.regex.Pattern;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.sql.DefaultStatementAdapter;
import org.nutz.dao.sql.FetchEntityCallback;
import org.nutz.dao.sql.FetchIntegerCallback;
import org.nutz.dao.sql.QueryEntityCallback;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.dao.sql.SqlImpl;
import org.nutz.dao.sql.SqlLiteral;

public class Sqls {

	public static Sql create(String sql) {
		return new SqlImpl(new SqlLiteral().valueOf(sql), DefaultStatementAdapter.ME);
	}

	public static Sql fetchEntity(String sql) {
		return create(sql).setCallback(callback.fetchEntity());
	}

	public static Sql fetchInt(String sql) {
		return create(sql).setCallback(callback.integer());
	}

	public static Sql queryEntity(String sql) {
		return create(sql).setCallback(callback.queryEntity());
	}

	public static CallbackFactory callback = new CallbackFactory();

	public static class CallbackFactory {
		public SqlCallback fetchEntity() {
			return new FetchEntityCallback();
		}

		public SqlCallback integer() {
			return new FetchIntegerCallback();
		}

		public SqlCallback queryEntity() {
			return new QueryEntityCallback();
		}
	}

	private static final Pattern CND = Pattern.compile("^([ \t]*)(WHERE|ORDER BY)(.+)$",
			Pattern.CASE_INSENSITIVE);

	public static String getConditionString(Entity<?> en, Condition condition) {
		if (null != condition) {
			String cnd = condition.toSql(en);
			if (cnd != null) {
				if (!CND.matcher(cnd).find())
					return " WHERE " + cnd;
				return cnd;
			}
		}
		return null;
	}
}
