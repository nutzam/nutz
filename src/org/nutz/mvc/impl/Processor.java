package org.nutz.mvc.impl;

import org.nutz.mvc.ActionContext;

public interface Processor {

	Processor process(ActionContext ac) throws Throwable;
	
	void setNext(Processor p);
	
	Processor getNext();

}
