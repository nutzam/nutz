package org.nutz.mvc.impl.processor;

import java.lang.reflect.Method;

import org.nutz.mvc.ActionContext;

public class DynamicModuleProcessor extends AbstractProcessor {

	private Class<?> moduleType;

	private String moduleName;

	private Method method;

	public DynamicModuleProcessor(Class<?> moduleType, String moduleName, Method method) {
		this.moduleType = moduleType;
		this.moduleName = moduleName;
		this.method = method;
	}

	public void process(ActionContext ac) throws Throwable {
		Object module = ac.getIoc().get(moduleType, moduleName);
		ac.setModule(module);
		ac.setMethod(method);
		doNext(ac);
	}

}
