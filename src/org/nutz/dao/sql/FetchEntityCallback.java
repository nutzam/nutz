package org.nutz.dao.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.nutz.dao.entity.Entity;

public class FetchEntityCallback extends EntityCallback {

	protected Object process(ResultSet rs, Entity<?> entity, SqlContext context) throws SQLException {
		if (rs.first())
			return entity.getObject(rs, context.getMatcher());
		return null;
	}

}
