package org.nutz.ioc.weaver;

import org.nutz.ioc.IocEventTrigger;
import org.nutz.ioc.IocMaking;
import org.nutz.ioc.ObjectWeaver;
import org.nutz.log.Log;
import org.nutz.log.LogFactory;

public class StaticWeaver implements ObjectWeaver {

	private static final Log log = LogFactory.getLog(StaticWeaver.class);

	private IocEventTrigger<Object> depose;
	private Object obj;

	public StaticWeaver(Object obj, IocEventTrigger<Object> depose) {
		this.depose = depose;
		this.obj = obj;
	}

	public void depose() {
		if (log.isDebugEnabled())
			if (null != depose) {
				log.debugf("\t >> ...");
				depose.trigger(obj);
				log.debugf("\t >> Done!");

			} else {
				log.debug("\t >> Nothing need to do");
			}
	}

	public Object weave(IocMaking ing) {
		return obj;
	}

}
