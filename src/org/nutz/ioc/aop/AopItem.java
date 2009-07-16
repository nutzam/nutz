package org.nutz.ioc.aop;

import java.util.Map;

public class AopItem {

	private Class<? extends MethodListenerFactory> factoryType;
	private Map<String, Object> init;
	private AopHook[] hooks;

	public Class<? extends MethodListenerFactory> getFactoryType() {
		return factoryType;
	}

	public Map<String, Object> getInit() {
		return init;
	}

	public AopHook[] getHooks() {
		return hooks;
	}

}
