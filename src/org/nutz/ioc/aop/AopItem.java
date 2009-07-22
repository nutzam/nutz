package org.nutz.ioc.aop;

import java.util.Map;

import org.nutz.aop.MethodListener;

public class AopItem {

	private Class<? extends ObjectHookingFactory> factoryType;
	private Class<? extends MethodListener> type;
	private Object[] args;
	private Map<String, Object> init;
	private AopHook[] hooks;

	public Class<? extends MethodListener> getType() {
		return type;
	}

	public Object[] getArgs() {
		return args;
	}

	public Class<? extends ObjectHookingFactory> getFactoryType() {
		return factoryType;
	}

	public Map<String, Object> getInit() {
		return init;
	}

	public AopHook[] getHooks() {
		return hooks;
	}

}
