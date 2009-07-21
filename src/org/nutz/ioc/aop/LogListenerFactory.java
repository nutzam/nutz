package org.nutz.ioc.aop;

import java.util.Map;

public class LogListenerFactory implements MethodListenerFactory {

	@Override
	public AopMethodPair[] getListener(Map<String, Object> init, AopHook[] hooks) {
		AopMethodPair[] array = new AopMethodPair[hooks.length];
		for(int i=0;i<hooks.length;i++){
			AopHook hook = hooks[i];
		}
		return array;
	}

}
