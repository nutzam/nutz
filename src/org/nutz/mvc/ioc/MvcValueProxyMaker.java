package org.nutz.mvc.ioc;

import org.nutz.ioc.IocMaking;
import org.nutz.ioc.ValueProxy;
import org.nutz.ioc.ValueProxyMaker;
import org.nutz.ioc.meta.IocValue;
import org.nutz.mvc.ioc.val.RequestValue;
import org.nutz.mvc.ioc.val.SessionValue;

public class MvcValueProxyMaker implements ValueProxyMaker {

	public ValueProxy make(IocMaking ing, IocValue iv) {
		if ("mvc".equalsIgnoreCase(iv.getType())){
			String renm = iv.getValue().toString().toLowerCase();
			if ("$request".equals(renm))
				return new RequestValue();
			if ("$session".equals(renm))
				return new SessionValue();
		}
		return null;
	}

	public String[] supportedTypes() {
		return new String[]{"mvc"};
	}

}
