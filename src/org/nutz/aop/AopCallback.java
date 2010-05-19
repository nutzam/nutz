package org.nutz.aop;

public interface AopCallback {
	
	Object _aop_invoke(int methodIndex,Object [] args);

}
