package com.zzh.ioc;

import java.util.List;

import com.zzh.lang.Mirror;

public interface Mapping {

	boolean isSingleton();

	String getName();

	Mirror<?> getMirror();

	List<MappingField> getFields();

}
