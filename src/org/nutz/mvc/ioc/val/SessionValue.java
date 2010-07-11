package org.nutz.mvc.ioc.val;

import org.nutz.ioc.IocContext;
import org.nutz.ioc.IocMaking;
import org.nutz.ioc.ValueProxy;
import org.nutz.ioc.impl.ComboContext;
import org.nutz.mvc.ioc.SessionIocContext;

public class SessionValue implements ValueProxy {

	public Object get(IocMaking ing) {
		SessionIocContext sessionIocContext = unwarpIocContext(ing.getContext());
		if (sessionIocContext != null)
			return sessionIocContext.getSession();
		return null;
	}

	public SessionIocContext unwarpIocContext(IocContext context){
		if (context instanceof ComboContext)
			for (IocContext iocContext : ((ComboContext)context).getContexts()) {
				if (iocContext instanceof SessionIocContext)
					return (SessionIocContext)iocContext;
				if (iocContext instanceof ComboContext)
					return unwarpIocContext(iocContext);
			}
		return null;
	}
}
