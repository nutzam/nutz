package com.zzh.dao.callback;

import java.sql.Connection;

public interface ConnCallback {
	
	void invoke(Connection conn) throws Exception;
	
}
