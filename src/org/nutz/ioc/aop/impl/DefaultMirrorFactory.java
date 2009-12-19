package org.nutz.ioc.aop.impl;

import org.nutz.aop.ClassAgent;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.aop.MirrorFactory;
import org.nutz.lang.Mirror;
import org.nutz.log.Logs;
import org.nutz.plugin.NoPluginCanWorkException;
import org.nutz.plugin.SimplePluginManager;

/**
 * TODO: 这个类还需要吗??
 * @author zozoh(zozohtnt@gmail.com)
 * @author Wendal(wendal1985@gmail.com)
 */
public class DefaultMirrorFactory implements MirrorFactory {
	
	private ClassAgent agent;

	public DefaultMirrorFactory(Ioc ioc) {
		try{
			this.agent = new SimplePluginManager<ClassAgent>("org.nutz.aop.asm.AsmClassAgent").get();
			this.agent.setIoc(ioc);
		}catch (NoPluginCanWorkException e) {
			Logs.getLog(getClass()).warn("No Aop plugin can work. Aop is disable now.",e);
		}
	}

	public <T> Mirror<T> getMirror(Class<T> type, String name) {
		if (agent != null)
			return Mirror.me(agent.define(type));
		return Mirror.me(type);
	}
}
