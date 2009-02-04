package com.zzh.ioc;

import java.util.List;

public interface Assemble {

	Mapping getMapping(String name);
	
	List<String> names();

}
