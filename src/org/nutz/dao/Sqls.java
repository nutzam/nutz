package org.nutz.dao;

import java.util.List;
import java.util.regex.Pattern;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.sql.DefaultStatementAdapter;
import org.nutz.dao.sql.FetchEntityCallback;
import org.nutz.dao.sql.FetchIntegerCallback;
import org.nutz.dao.sql.QueryEntityCallback;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.dao.sql.SqlImpl;
import org.nutz.dao.sql.SqlLiteral;
import org.nutz.dao.tools.DTable;
import org.nutz.dao.tools.DTableParser;
import org.nutz.dao.tools.TableSqlMaker;
import org.nutz.dao.tools.impl.NutDTableParser;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;

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

	public static void executeDefinition(Dao dao, String dods) {
		DTableParser parser = new NutDTableParser();
		TableSqlMaker maker = TableSqlMaker.newInstance(((NutDao) dao).meta());
		List<DTable> dts = parser.parse(dods);
		for (DTable dt : dts) {
			if (dao.exists(dt.getName()))
				dao.clear(dt.getName());
			else {
				Sql c = maker.makeCreateSql(dt);
				dao.execute(c);
			}
		}
	}

	public static void executeDefinitionFile(Dao dao, String dodPath) {
		String sqls = Lang.readAll(Streams.fileInr(dodPath));
		Sqls.executeDefinition(dao, sqls);
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
