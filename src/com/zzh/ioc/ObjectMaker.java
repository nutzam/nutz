package com.zzh.ioc;

import java.util.Map;

public abstract class ObjectMaker {

	protected abstract boolean accept(Map<String, Object> properties);

	protected abstract Object make(Map<String, Object> properties);

}
