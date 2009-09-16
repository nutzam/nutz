package org.nutz.dao.entity;

import org.nutz.dao.Database;
import org.nutz.dao.TableName;
import org.nutz.dao.sql.SQLs;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.segment.CharSegment;
import org.nutz.lang.segment.Segment;

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
			throw new RuntimeException("Wrong entity @Id defination!" + " The 'next' property must be name/value pair,"
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

	abstract Sql sql();

	/*----------------------------------------------------------------*/
	static class Static extends Next {

		Sql sql;

		Static(String s) {
			sql = SQLs.fetchInt(s);
		}

		Sql sql() {
			return sql;
		}
	}

	/*----------------------------------------------------------------*/
	static class DefaultStatic extends Static {
		DefaultStatic(String tableName, String idName) {
			super(format("SELECT MAX(%s) FROM %s", idName, tableName));
		}
	}

	/*----------------------------------------------------------------*/
	static class Dynamic extends Next {

		private Segment seg;

		Dynamic(Segment seg) {
			this.seg = seg;
		}

		Sql sql() {
			Sql sql = SQLs.fetchInt((TableName.render(seg)));
			return sql;
		}
	}

	/*----------------------------------------------------------------*/
	static class DefaultDynamic extends Dynamic {

		DefaultDynamic(String tableName, String idName) {
			super(new CharSegment(format("SELECT MAX(%s) FROM %s", idName, tableName)));
		}

	}
	/*----------------------------------------------------------------*/

}
