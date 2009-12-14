package org.nutz.ioc.weaver;

import org.nutz.ioc.IocEventTrigger;
import org.nutz.ioc.IocMaking;
import org.nutz.ioc.ObjectWeaver;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class StaticWeaver implements ObjectWeaver {

	private static final Log log = Logs.getLog(StaticWeaver.class);

	private IocEventTrigger<Object> depose;
	private Object obj;

	public StaticWeaver(Object obj, IocEventTrigger<Object> depose) {
		this.depose = depose;
		this.obj = obj;
	}

	public void depose() {
		if (null != depose) {
			if (log.isDebugEnabled())
				log.debugf("\t >> do depose ...");

			depose.trigger(obj);

			if (log.isDebugEnabled())
				log.debugf("\t >> Done!");
		} else if (log.isDebugEnabled())
			log.debug("\t >> Nothing need to do");
	}

	public Object weave(IocMaking ing) {
		return obj;
	}

}
