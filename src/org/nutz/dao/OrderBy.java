package org.nutz.dao;

import org.nutz.dao.Condition;

public interface OrderBy extends Condition {

	OrderBy asc(String name);

	OrderBy desc(String name);
}
