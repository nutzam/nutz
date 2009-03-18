package com.zzh.ioc;

import java.util.Map;

import com.zzh.lang.Mirror;

public abstract class ObjectMaker<T> {

	private Mirror<T> mirror;

	@SuppressWarnings("unchecked")
	public ObjectMaker() {
		mirror = Mirror.me((Class<T>) Mirror.getTypeParams(getClass())[0]);
	}

	public Mirror<T> getMirror() {
		return mirror;
	}

	public Class<T> getType() {
		return mirror.getType();
	}

	protected abstract boolean accept(Map<String, Object> properties);

	protected abstract T make(Map<String, Object> properties);

}
