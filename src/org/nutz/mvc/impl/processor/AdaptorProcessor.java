package org.nutz.mvc.impl.processor;

import java.util.List;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.HttpAdaptor;

public class AdaptorProcessor extends AbstractProcessor {

	private HttpAdaptor adaptor;

	public AdaptorProcessor(HttpAdaptor adaptor) {
		this.adaptor = adaptor;
	}

	public void process(ActionContext ac) throws Throwable {
		List<String> phArgs = ac.getPathArgs();
		Object[] args = adaptor.adapt(	ac.getServletContext(),
										ac.getRequest(),
										ac.getResponse(),
										phArgs.toArray(new String[phArgs.size()]));
		ac.setMethodArgs(args);
		doNext(ac);
	}

}
