package org.nutz.ioc.weaver;

import org.nutz.ioc.IocEventTrigger;
import org.nutz.ioc.IocMaking;
import org.nutz.ioc.ObjectWeaver;

public class StaticWeaver implements ObjectWeaver {

	private IocEventTrigger<Object> depose;
	private Object obj;

	StaticWeaver(Object obj, IocEventTrigger<Object> depose) {
		this.depose = depose;
		this.obj = obj;
	}

	public void deose() {
		if (null != depose)
			depose.trigger(obj);
	}

	public Object weave(IocMaking ing) {
		return obj;
	}

}
