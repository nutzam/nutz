package org.nutz.mvc.impl.processor;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.impl.Processor;

public abstract class AbstractProcessor implements Processor {

	private Processor next;

	public void setNext(Processor next) {
		this.next = next;
	}

	public Processor getNext() {
		return next;
	}

	public Processor process(ActionContext ac) throws Throwable {
		doProcess(ac);
		return next;
	}

	public abstract void doProcess(ActionContext ac) throws Throwable;

}
