package com.zzh.ioc;

import java.util.List;
import java.util.Map;

public interface MappingValue {

	Object getAsObject();

	List<?> getAsList();

	Map<?, ?> getAsMap();

	Object[] getAsArray();

}
