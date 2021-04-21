package org.nutz.dao.sql;

import org.nutz.dao.Condition;
import org.nutz.dao.util.lambda.PFun;

public interface GroupBy extends OrderBy {

	GroupBy groupBy(String ... names);

	<T> GroupBy groupBy(PFun<T, ?>... names);

	GroupBy having(Condition cnd);
}
