package org.nutz.ioc.aop;

import java.util.Map;

import org.nutz.aop.ClassAgent;
import org.nutz.aop.MethodListener;
import org.nutz.ioc.Ioc;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;

public class MirrorFactory {

	private Ioc ioc;

	public MirrorFactory(Ioc ioc) {
		this.ioc = ioc;
		AopSetting as = ioc.get(AopSetting.class, "$aop");
		if (null != as.getItems())
			try {
				for (AopItem ai : as.getItems()) {
					// find log
					MethodListenerFactory mlf = ai.getFactoryType().newInstance();
					AopMethodPair[] lstn = mlf.getListener(ai.getInit(), ai.getHooks());
					
					// find another method listener
					// set mirror factory to Ioc
				}
			} catch (Exception e) {
				throw Lang.wrapThrow(e);
			}
	}

	public <T> Mirror<T> getMirror(Class<T> type, String name) {
		return null;
	}

}
