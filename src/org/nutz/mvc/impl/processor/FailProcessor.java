package org.nutz.mvc.impl.processor;

import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.NutConfig;

public class FailProcessor extends ViewProcessor {

	@Override
	public void init(NutConfig config, ActionInfo ai) throws Throwable {
		view = evalView(config, ai, ai.getFailView());
	}
}
