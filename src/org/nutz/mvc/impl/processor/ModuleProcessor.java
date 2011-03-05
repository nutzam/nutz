package org.nutz.mvc.impl.processor;

import java.lang.reflect.Method;

import org.nutz.lang.Strings;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.NutConfig;

/**
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class ModuleProcessor extends AbstractProcessor {
	
	private Object module;
	
	private String injectName;
	
	private Class<?> moduleType;
	private Method method;
	
	@Override
	public void init(NutConfig config, ActionInfo ai) throws Throwable {
		method = ai.getMethod();
		if (Strings.isBlank(ai.getInjectName())) {
			moduleType = ai.getModuleType();
			injectName = ai.getInjectName();
		} else
			module = ai.getModuleType().newInstance();
	}

	public void process(ActionContext ac) throws Throwable {
		ac.setModule(module == null ? ac.getIoc().get(moduleType, injectName) : module);
		ac.setMethod(method);
		doNext(ac);
	}
	
}
