package org.nutz.dao.sql;

public class SQLs {

	private static DefaultStatementAdapter ADAPTER = new DefaultStatementAdapter();

	public static Sql create(String sql) {
		return create(new SqlLiteral().valueOf(sql));
	}

	public static Sql create(SqlLiteral sql) {
		return new SqlImpl(sql, ADAPTER);
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

	public static __ callback = new __();

	public static class __ {
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

}
