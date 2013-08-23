package org.nutz.dao.sql;

import org.nutz.dao.Condition;

public interface GroupBy extends Condition,PItem {

	GroupBy groupBy(String ... names);
	
	GroupBy having(Condition cnd);
}
