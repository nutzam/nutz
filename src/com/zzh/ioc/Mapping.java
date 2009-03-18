package com.zzh.ioc;

import java.util.Map;

public interface Mapping {

	Class<?> getObjectType();

	boolean isSingleton();

	void setSingleton(boolean sg);

	Object[] getBorningArguments();

	Map<String, Object> getFieldsSetting();

}
