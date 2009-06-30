package org.nutz.ioc.impl;

import org.nutz.ioc.Events;

class ObjectHolder<T> {

	ObjectHolder(T obj, Events<T> events) {
		this.obj = obj;
		this.events = events;
		if (null != events)
			if (null != events.getWhenCreate())
				events.getWhenCreate().invoke(obj);
	}

	private T obj;
	private Events<T> events;

	T getObject() {
		if (null != events)
			if (null != events.getWhenFetch())
				events.getWhenFetch().invoke(obj);
		return obj;
	}

	void depose() {
		if (null != events)
			if (null != events.getWhenDepose())
				events.getWhenDepose().invoke(obj);
	}

}
