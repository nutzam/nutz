package org.nutz.mvc.impl.processor;

import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.NutConfig;

/**
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class FailProcessor extends ViewProcessor {

	@Override
	public void init(NutConfig config, ActionInfo ai) throws Throwable {
		view = evalView(config, ai, ai.getFailView());
	}
}
