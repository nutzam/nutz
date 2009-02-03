package com.zzh.ioc;

import java.lang.reflect.Field;
import java.util.Map;

import com.zzh.lang.Mirror;

public interface Mapping {

	boolean isSingleton();

	String getName();

	Mirror<?> getType();

	Map<Field, Value> getMappingFields();

	Value[] getConstructorArguments();

}
