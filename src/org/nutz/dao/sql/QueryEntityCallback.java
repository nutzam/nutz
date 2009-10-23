package org.nutz.dao.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.nutz.dao.Pager;
import org.nutz.dao.entity.Entity;

public class QueryEntityCallback extends EntityCallback {

	@Override
	protected Object process(ResultSet rs, Entity<?> entity, SqlContext context) throws SQLException {
		Pager pager = context.getPager();
		List<Object> list = new LinkedList<Object>();
		if (null == pager || !pager.isDefault()) {
			while (rs.next()) {
				list.add(entity.getObject(rs, context.getMatcher()));
			}
		} else if (rs.last()) {
			if (pager.getPageSize() > 1000)
				rs.setFetchSize(20);
			else
				rs.setFetchSize(pager.getPageSize());
			LoopScope ls = LoopScope.eval(pager, rs.getRow());
			if (rs.absolute(ls.start + 1))
				for (int i = ls.start; i < ls.max; i++) {
					Object o = entity.getObject(rs, context.getMatcher());
					list.add(o);
					if (!rs.next())
						break;
				}
		}
		return list;
	}

}
