package org.nutz.dao;

public interface OrderBy extends Condition {

	OrderBy asc(String name);

	OrderBy desc(String name);
}
