package org.nutz.dao.util.cri;

import org.nutz.dao.Condition;
import org.nutz.dao.DaoException;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.impl.sql.pojo.NoParamsPItem;
import org.nutz.lang.Lang;

public class GroupBySet extends NoParamsPItem {

	private String[] names;
	
	private Condition having;
	
	public GroupBySet(String...names) {
		if (Lang.length(names) == 0)
			throw new DaoException("NULL for GroupBy");
		this.names = names;
	}
	
	public void having(Condition cnd) {
		having = cnd;
	}
	
	@Override
	public void joinSql(Entity<?> en, StringBuilder sb) {
		sb.append(" GROUP BY ");
		for (String name : names) {
			MappingField field = en != null ? en.getColumn(name) : null;
			if (field != null)
				sb.append(field.getColumnName());
			else
				sb.append(name);
			sb.append(",");
		}
		sb.setCharAt(sb.length() - 1, ' ');
		if (having != null) {
			sb.append("HAVING ");
			if (having instanceof SqlExpressionGroup) {
				((SqlExpressionGroup)having).setTop(false);
				sb.append(having.toSql(en));
			} else {
				String sql = having.toSql(en).trim();
				if (sql.length() > 5 && "WHERE".equalsIgnoreCase(sql.substring(0,  5)))
					sql = sql.substring(5);
				sb.append(sql);
			}
		}
	}
}
