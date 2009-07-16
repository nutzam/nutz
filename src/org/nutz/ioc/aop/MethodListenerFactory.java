package org.nutz.ioc.aop;

import org.nutz.aop.MethodListener;

public interface MethodListenerFactory {

	MethodListener getListener(AopItem ai);
	
}
