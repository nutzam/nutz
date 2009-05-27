package com.zzh.ioc;

import com.zzh.ioc.meta.Obj;

public interface ObjLoader {

	Obj load(String name);

	String[] keys();

}
