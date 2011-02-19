package org.nutz.mvc.impl.processor;

import java.lang.reflect.Method;

import org.nutz.mvc.ActionContext;

public class StaticModuleProcessor extends AbstractProcessor{

	private Object module;
	private Method method;

	public StaticModuleProcessor(Object module, Method method) {
		this.module = module;
		this.method = method;
	}

	public void doProcess(ActionContext ac) throws Throwable {
		ac.setModule(module);
		ac.setMethod(method);
	}

}
