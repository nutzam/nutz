package org.nutz.dao.entity;

import java.sql.ResultSet;

import org.nutz.dao.FieldMatcher;

public interface Borning {
	
	Object born(ResultSet rs, FieldMatcher fm) throws Exception;
	
}
