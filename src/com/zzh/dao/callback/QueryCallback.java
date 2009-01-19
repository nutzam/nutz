package com.zzh.dao.callback;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface QueryCallback<T> {
	
	T invoke(ResultSet rs) throws SQLException;
	
}
