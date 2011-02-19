package org.nutz.mvc.impl.processor;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.impl.Processor;

public abstract class AbstractProcessor implements Processor {

	private Processor next;

	public void setNext(Processor next) {
		this.next = next;
	}

	protected void doNext(ActionContext ac) throws Throwable {
		if (null != next)
			next.process(ac);
	}

}
