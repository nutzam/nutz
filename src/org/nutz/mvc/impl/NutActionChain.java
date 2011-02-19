package org.nutz.mvc.impl;

import java.util.List;

import org.nutz.lang.Lang;
import org.nutz.mvc.ActionChain;
import org.nutz.mvc.ActionContext;

public class NutActionChain implements ActionChain {

	private List<Processor> list;

	private Processor errorProcessor;

	public NutActionChain(List<Processor> list, Processor errorProcessor) {
		this.list = list;
		this.errorProcessor = errorProcessor;
	}

	public void doChain(ActionContext ac) {
		try {
			for (Processor p : list) {
				if (!p.process(ac))
					break;
			}
		}
		catch (Throwable e) {
			ac.setError(e);
			try {
				errorProcessor.process(ac);
			}
			catch (Throwable ee) {
				throw Lang.wrapThrow(ee);
			}
		}
	}

}
