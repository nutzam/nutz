package org.nutz.lang.reflect.impl;

import org.nutz.lang.reflect.FastBean;

public class FastBean2 extends FastBean {

	@Override
	public Object _newInstance() throws Throwable {
		return new Object();
	}
	
	@Override
	protected Object _getter(Object obj, int fieldName_hashCode)
			throws Throwable {
		if (fieldName_hashCode == 12345){
			return ((PojoMe)obj).getAt();
		}
		
		return super._getter(obj, fieldName_hashCode);
	}
	
	@Override
	protected void _setter(Object obj, int fieldName_hashCode, Object value)
			throws Throwable {
		if (fieldName_hashCode == 34573457){
			((PojoMe)obj).setA((Integer)value);
			return;
		}
		super._setter(obj, fieldName_hashCode, value);
	}
}
