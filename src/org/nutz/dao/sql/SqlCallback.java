package org.nutz.dao.sql;

import java.sql.Connection;
import java.sql.SQLException;

public interface SqlCallback {

	void invoke(Connection conn) throws SQLException;

}
