package org.nutz.mvc.impl;

import org.nutz.mvc.ActionContext;

public interface Processor {

	boolean process(ActionContext ac) throws Throwable;

}
