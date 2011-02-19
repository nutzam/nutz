package org.nutz.mvc.impl.processor;

import java.lang.reflect.Method;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.impl.Processor;

public class StaticModuleProcessor implements Processor {

	private Object module;
	private Method method;

	public StaticModuleProcessor(Object module, Method method) {
		this.module = module;
		this.method = method;
	}

	public boolean process(ActionContext ac) throws Throwable {
		ac.setModule(module);
		ac.setMethod(method);
		return true;
	}

}
