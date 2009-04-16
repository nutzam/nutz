package com.zzh.dao.entity;

import com.zzh.dao.Database;
import com.zzh.dao.FetchSql;
import com.zzh.dao.Sql;
import com.zzh.dao.TableName;
import com.zzh.dao.impl.NutDao;
import com.zzh.lang.segment.CharSegment;
import com.zzh.lang.segment.Segment;

import static java.lang.String.*;

abstract class Next {

	static Next create(Database db, EntityName entityName, String[] next, String columnName) {
		if (next.length == 0) { // Default
			return defaultNext(entityName, columnName);
		} else if (next.length == 1) { // Single value
			return customizedNext(next[0]);
		} else if (next.length % 2 == 0) { // Name/Value
			for (int i = 0; i < next.length; i++)
				if (i % 2 == 0 && db.name().equalsIgnoreCase(next[i]))
					return customizedNext(next[i + 1]);
		} else { // Syntax Error
			throw new RuntimeException("Wrong entity @Id defination!"
					+ " The 'next' property must be name/value pair,"
					+ " or single value indicate how to fetch Id sequence,"
					+ " or empty as defaul to use 'SELECT MAX(@Id) FROM @Table'");
		}
		return defaultNext(entityName, columnName);
	}

	private static Next customizedNext(String s) {
		CharSegment seg = new CharSegment(s);
		if (seg.keys().size() == 0)
			return new Static(s);
		else
			return new Dynamic(seg);
	}

	private static Next defaultNext(EntityName name, String columnName) {
		if (name instanceof EntityName.DynamicEntityName)
			return new DefaultDynamic(name.orignalString(), columnName);
		else
			return new DefaultStatic(name.value(), columnName);
	}

	abstract Sql<Integer> sql();

	/*----------------------------------------------------------------*/
	static class Static extends Next {
		Static(String s) {
			sql = new FetchSql<Integer>().setCallback(NutDao.evalResultSetAsInt).valueOf(s);
		}

		Sql<Integer> sql;

		Sql<Integer> sql() {
			return sql;
		}
	}

	/*----------------------------------------------------------------*/
	static class DefaultStatic extends Static {
		DefaultStatic(String tableName, String idName) {
			super(format("SELECT MAX(%s) FROM %s;", idName, tableName));
		}
	}

	/*----------------------------------------------------------------*/
	static class Dynamic extends Next {
		Dynamic(Segment seg) {
			this.seg = seg;
		}

		private Segment seg;

		Sql<Integer> sql() {
			Sql<Integer> sql = new FetchSql<Integer>().setCallback(NutDao.evalResultSetAsInt);
			sql.valueOf(TableName.render(seg));
			return sql;
		}
	}

	/*----------------------------------------------------------------*/
	static class DefaultDynamic extends Dynamic {

		DefaultDynamic(String tableName, String idName) {
			super(new CharSegment(format("SELECT MAX(%s) FROM %s;", idName, tableName)));
		}

	}
	/*----------------------------------------------------------------*/

}
