package org.nutz.dao.sql;

import org.nutz.dao.Condition;

public interface GroupBy extends OrderBy {

	GroupBy groupBy(String ... names);
	
	GroupBy having(Condition cnd);
}
