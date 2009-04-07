package com.zzh.ioc;

public interface MappingLoader {

	Mapping load(String name);
	
	String[] keys();
}
