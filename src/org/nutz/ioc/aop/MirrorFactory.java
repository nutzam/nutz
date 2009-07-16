package org.nutz.ioc.aop;

import java.util.Map;

import org.nutz.aop.ClassAgent;
import org.nutz.ioc.Ioc;
import org.nutz.lang.Mirror;

public class MirrorFactory {

	private Ioc ioc;
	

	public MirrorFactory(Ioc ioc) {
		this.ioc = ioc;
		// find log
		// find another method listener
		// set mirror factory to Ioc
	}

	public <T> Mirror<T> getMirror(Class<T> type, String name) {
		return null;
	}

}
