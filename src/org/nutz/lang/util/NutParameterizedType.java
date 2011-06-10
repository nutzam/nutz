package org.nutz.lang.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class NutParameterizedType implements ParameterizedType {
	
	public static Type list(Type clazz){
		NutParameterizedType type = new NutParameterizedType();
		type.rawType = List.class;
		type.setActualTypeArguments(clazz);
		return type;
	}
	
	public static Type map(Type key, Type value){
		NutParameterizedType type = new NutParameterizedType();
		type.rawType = Map.class;
		type.setActualTypeArguments(key,value);
		return type;
	}
	
	private Type[] actualTypeArguments;
	
	private Type rawType;
	
	private Type ownerType;

	@Override
	public Type[] getActualTypeArguments() {
		return actualTypeArguments;
	}

	@Override
	public Type getRawType() {
		return rawType;
	}

	@Override
	public Type getOwnerType() {
		return ownerType;
	}

	public void setActualTypeArguments(Type...actualTypeArguments) {
		this.actualTypeArguments = actualTypeArguments;
	}
	
	public void setOwnerType(Type ownerType) {
		this.ownerType = ownerType;
	}
	
	public void setRawType(Type rawType) {
		this.rawType = rawType;
	}
}
