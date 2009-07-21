package org.nutz.ioc.aop;

import java.util.Map;

public interface MethodListenerFactory {

	AopMethodPair[] getListener(Map<String, Object> init, AopHook[] hooks);

}
