package org.nutz.dao.callback;

import java.sql.Connection;

public abstract class ConnCallback{
	
	public abstract void invoke(Connection conn) throws Exception;

}
