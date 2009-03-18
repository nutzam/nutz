package com.zzh.dao.callback;

import java.sql.Connection;

public interface SqlCallback {

	Object invoke(Connection conn) throws Exception;
	
}
