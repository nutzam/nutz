package org.nutz.dao.pager;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.EntityField;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

public class SqlServer2005Pager extends AbstractPager {

	private static final String PTN =
	// <with>
	"WITH __tmp as ("
	// <query>
			+ "SELECT %s, ROW_NUMBER() OVER (%s) AS __rowid FROM %s %s"
			// </query>
			+ ")"
			// </with>
			+ " SELECT * FROM __tmp WHERE __rowid BETWEEN %d AND %d";

	public String toSql(Entity<?> entity, String fields, String cnd) {
		if (null == entity) {
			throw Lang.makeThrow("%s can not create query SQL with entity", this.getClass()
																				.getSimpleName());
		}
		String where;
		String orderBy;
		// No condition, order by PK
		if (Strings.isBlank(cnd)) {
			where = "";
			orderBy = evalOrderBy(entity);
		}
		// Has condition
		else {
			String CND = cnd.toUpperCase();
			int pos = CND.indexOf("ORDER BY");
			// cnd didn't contains "ORDER BY"
			if (pos < 0) {
				where = cnd;
				orderBy = evalOrderBy(entity);
			}
			// cnd has "ORDER BY"
			else {
				where = cnd.substring(0, pos);
				orderBy = cnd.substring(pos);
			}
		}
		int from = this.getOffset();
		int to = from + this.getPageSize();
		from++;
		return String.format(PTN, fields, orderBy, entity.getViewName(), where, from, to);
	}

	private String evalOrderBy(Entity<?> entity) {
		// 单主键
		EntityField ef = entity.getIdentifiedField();
		if (null != ef)
			return "ORDER BY " + ef.getColumnName();

		// 复合主键
		EntityField[] pks = entity.getPkFields();
		if (null != pks && pks.length > 0) {
			StringBuilder sb = new StringBuilder("ORDER BY ");
			for (EntityField pk : pks)
				sb.append(pk.getColumnName()).append(",");
			sb.deleteCharAt(sb.length() - 1);
			sb.append(" ASC");
			return sb.toString();
		}

		// 老大，什么主键都没有，怎么分页啊！
		throw Lang.makeThrow(	"%s can not create query SQL for '%s':"
										+ " you have to define @Id or @Name or @PK for this entity",
								this.getClass().getSimpleName(),
								entity.getType().getName());
	}
}
