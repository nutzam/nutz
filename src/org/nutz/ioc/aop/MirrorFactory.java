package org.nutz.ioc.aop;

import org.nutz.aop.ClassAgent;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.Utils;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;

/**
 * @author zozohtnt
 * @author Wendal(wendal1985@gmail.com)
 *
 */
public class MirrorFactory {

	private AopObject[] objs;

	public void init(Ioc ioc, String aopObjName) {
		if (ioc.hasName(aopObjName)) {
			AopSetting as = ioc.get(AopSetting.class, aopObjName);
			if (null != as.getItems())
				for (AopItem ai : as.getItems()) {
					ObjectHookingFactory ohf = evalObjectHookingFactory(ai);
					ObjectHooking[] hookings = ohf.getHooking(ai);
					objs = new AopObject[hookings.length];
					for (int i = 0; i < hookings.length; i++) {
						ObjectHooking oh = hookings[i];
						ClassAgent ca = Utils.newDefaultClassAgent();
						for (ObjectMethodHooking omh : oh.getMethodHookings()) {
							ca.addListener(omh.getMatcher(), omh.getListener());
						}
						objs[i] = new AopObject(oh.getObjectMatcher(), ca);
					}
				}
		}
	}

	private static ObjectHookingFactory evalObjectHookingFactory(AopItem ai) {
		if (ai.getFactoryType() != null) {
			try {
				return ai.getFactoryType().newInstance();
			} catch (Exception e) {
				throw Lang.wrapThrow(e);
			}
		} else if (ai.getType() != null) {
			return new DefaultHookingFactory();
		}
		throw Lang.makeThrow("Don't know how to evaluate $aop, lack @type or @factoryType");
	}

	public <T> Mirror<T> getMirror(Class<T> type, String name) {
		if (null != objs && name != null && name.length() > 0 && name.charAt(0) != '$')
			for (AopObject ao : objs)
				if (ao.getObjectMatcher().match(type, name)) {
					return Mirror.me(ao.getClassAgent().define(type));
				}
		return Mirror.me(type);
	}
}
