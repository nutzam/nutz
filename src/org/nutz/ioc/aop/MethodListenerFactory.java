package org.nutz.ioc.aop;

import java.util.Map;

import org.nutz.aop.MethodListener;

public interface MethodListenerFactory {

	<T extends MethodListener> Class<T> getListnerType();

	MethodListener getListener(Map<String, Object> init, AopHook[] hooks);

}
