package org.nutz.ioc.aop;

import java.util.Map;

import org.nutz.aop.MethodListener;
import org.nutz.log.aop.LogListener;

public class LogListenerFactory implements MethodListenerFactory {
	
	@Override
	public <T extends MethodListener> Class<T> getListnerType() {
		return null;
	}

	@Override
	public MethodListener getListener(Map<String, Object> init, AopHook[] hooks) {
		return null;
	}

}
