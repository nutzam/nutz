package org.nutz.mvc.impl.processor;

import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.NutConfig;

/**
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class FailProcessor extends ViewProcessor {
	
	private static final Log log = Logs.getLog(FailProcessor.class);

	@Override
	public void init(NutConfig config, ActionInfo ai) throws Throwable {
		view = evalView(config, ai, ai.getFailView());
	}
	
	@Override
	public void process(ActionContext ac) throws Throwable {
		if (log.isDebugEnabled())
			log.debug("Catch handle error", ac.getError());
		super.process(ac);
	}
}
