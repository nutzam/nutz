package org.nutz.dao.sql;

public class SQLs {

	private static DefaultFieldTypeAdapter ADAPTER = new DefaultFieldTypeAdapter();

	public static Sql create(String sql) {
		return create(sql);
	}

	public static Sql create(SqlLiteral sql) {
		return new SqlImpl(sql, ADAPTER);
	}

	public static Sql fetch(String sql) {
		return create(sql).setCallback(callback.fetch());
	}

	public static Sql fetchInt(String sql) {
		return create(sql).setCallback(callback.integer());
	}

	public static Sql query(String sql) {
		return create(sql).setCallback(callback.query());
	}

	public static __ callback = new __();

	public static class __ {
		public SqlCallback fetch() {
			return new FetchEntityCallback();
		}

		public SqlCallback integer() {
			return new FetchIntegerCallback();
		}

		public SqlCallback query() {
			return new QueryEntityCallback();
		}
	}

}
