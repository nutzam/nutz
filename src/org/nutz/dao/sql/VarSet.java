package org.nutz.dao.sql;

import java.util.Map;
import java.util.Set;

public interface VarSet {

	VarSet set(String name, Object value);
	
	VarSet putAll(Map<String,Object> map);

	Object get(String name);

	Set<String> keys();

}
