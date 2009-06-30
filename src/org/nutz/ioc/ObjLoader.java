package org.nutz.ioc;

import org.nutz.ioc.meta.Obj;

public interface ObjLoader {

	Obj load(String name);

	String[] keys();

}
