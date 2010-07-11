package org.nutz.mvc.ioc.val;

import org.nutz.ioc.IocContext;
import org.nutz.ioc.IocMaking;
import org.nutz.ioc.ValueProxy;
import org.nutz.ioc.impl.ComboContext;
import org.nutz.mvc.ioc.RequestIocContext;

public class RequestValue implements ValueProxy {

	public Object get(IocMaking ing) {
		RequestIocContext requestIocContext = unwarpIocContext(ing.getContext());
		if (requestIocContext != null)
			return requestIocContext.getReq();
		return null;
	}

	public RequestIocContext unwarpIocContext(IocContext context){
		if (context instanceof ComboContext)
			for (IocContext iocContext : ((ComboContext)context).getContexts()) {
				if (iocContext instanceof RequestIocContext)
					return (RequestIocContext)iocContext;
				if (iocContext instanceof ComboContext)
					return unwarpIocContext(iocContext);
			}
		return null;
	}
}
