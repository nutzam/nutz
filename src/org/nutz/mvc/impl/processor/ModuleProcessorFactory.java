package org.nutz.mvc.impl.processor;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Processor;

public class ModuleProcessorFactory extends AbstractProcessor {
	
	private Processor processor;
	
	@Override
	public void init(NutConfig config, ActionInfo ai) throws Throwable {
		if (Strings.isBlank(ai.getInjectName())) {
			try {
				processor = new StaticModuleProcessor(ai.getModuleType().newInstance(), ai.getMethod());
			}
			catch (Exception e) {
				throw Lang.wrapThrow(	e,
										"Fail to create module '%s' by default constructor",
										ai.getModuleType());
			}
		} else {
			processor = new DynamicModuleProcessor(ai.getModuleType(),
												ai.getInjectName(),
												ai.getMethod());
		}
	}

	public void process(ActionContext ac) throws Throwable {
		doNext(ac);
	}

	@Override
	public void setNext(Processor next) {
		processor.setNext(next);
		super.setNext(processor);
	}
	
}
