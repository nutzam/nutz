package org.nutz.mvc.adaptor;

import java.lang.reflect.Type;
import java.util.Map;

import org.nutz.mvc.adaptor.injector.ArrayInjector;
import org.nutz.mvc.adaptor.injector.MapPairInjector;
import org.nutz.mvc.adaptor.injector.NameInjector;
import org.nutz.mvc.adaptor.injector.ObjectNavlPairInjector;
import org.nutz.mvc.adaptor.injector.ObjectPairInjector;
import org.nutz.mvc.adaptor.injector.PathArgInjector;
import org.nutz.mvc.annotation.Param;

/**
 * 将整个 HTTP 请求作为名值对来处理
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class PairAdaptor extends AbstractAdaptor {

	protected ParamInjector evalInjectorBy(Class<?> type, Param param, Type[] paramTypes) {
		if (null == param)
			return new PathArgInjector(type);
		String pm = param.value();
		// POJO
		if ("..".equals(pm)) {
			if (type.isAssignableFrom(Map.class))
				return new MapPairInjector();
			return new ObjectPairInjector(null, type);
		}
		// POJO with prefix
		else if (pm.startsWith("::") && pm.length() > 2) {
			return new ObjectNavlPairInjector(pm.substring(2), type);
		}
		// POJO[]
		else if (type.isArray())
			return new ArrayInjector(pm, type, paramTypes);

		// Name-value
		return new NameInjector(pm, type, paramTypes);
	}

}
