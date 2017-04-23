package org.nutz.dao;

import org.nutz.dao.Condition;

public interface Nesting {
	
	Nesting select(String names,Class<?>classOfT,Condition cnd);
	
}
