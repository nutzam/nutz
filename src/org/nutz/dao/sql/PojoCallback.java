package org.nutz.dao.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface PojoCallback {

	Object invoke(Connection conn, ResultSet rs, Pojo pojo) throws SQLException;
	
}
