package org.nutz.ioc.weaver;

import org.nutz.ioc.IocMaking;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
public class StaticWeaver extends DynamicWeaver {

	private static final Log log = Logs.getLog(StaticWeaver.class);

	private Object obj;

	private boolean inited = false;

	public void setObj(Object obj) {
		this.obj = obj;
		inited = true;
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
		if (inited == false)
			setObj(super.weave(ing));
		return obj;
	}

}
