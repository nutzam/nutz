package org.nutz.dao.sql;

import java.util.List;

public interface VarSet {

	VarSet set(String name, Object value);

	Object get(String name);

	List<String> keys();

}
