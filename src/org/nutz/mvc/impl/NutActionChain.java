package org.nutz.mvc.impl;

import java.util.Iterator;
import java.util.List;

import org.nutz.lang.Lang;
import org.nutz.mvc.ActionChain;
import org.nutz.mvc.ActionContext;

public class NutActionChain implements ActionChain {

	private Processor head;

	private Processor errorProcessor;

	public NutActionChain(List<Processor> list, Processor errorProcessor) {
		if (null != list) {
			Iterator<Processor> it = list.iterator();
			if (it.hasNext()) {
				head = it.next();
				Processor p = head;
				while (it.hasNext()) {
					Processor next = it.next();
					p.setNext(next);
					p = next;
				}
			}
		}
		this.errorProcessor = errorProcessor;
	}

	public void doChain(ActionContext ac) {
		if (null != head)
			try {
				head.process(ac);
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
