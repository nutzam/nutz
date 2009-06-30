package org.nutz.ioc;

import org.nutz.ioc.meta.Val;

public interface ValueMaker {

	String forType();
	
	Object make(Val val);
	
}
