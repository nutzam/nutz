package org.nutz.mvc.impl.processor;

import java.util.List;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.HttpAdaptor;
import org.nutz.mvc.impl.Processor;

public class AdaptorProcessor implements Processor {

	private HttpAdaptor adaptor;

	public AdaptorProcessor(HttpAdaptor adaptor) {
		this.adaptor = adaptor;
	}

	public boolean process(ActionContext ac) throws Throwable {
		List<String> phArgs = ac.getPathArgs();
		Object[] args = adaptor.adapt(	ac.getServletContext(),
										ac.getRequest(),
										ac.getResponse(),
										phArgs.toArray(new String[phArgs.size()]));
		ac.setMethodArgs(args);
		return true;
	}

}
