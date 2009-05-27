package com.zzh.ioc;

import com.zzh.ioc.meta.Val;

public interface ValueMaker {

	String forType();
	
	Object make(Val val);
	
}
