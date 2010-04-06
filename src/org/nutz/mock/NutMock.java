package org.nutz.mock;

public abstract class NutMock {
	
	public static <T> T create(Class<T> classZ){
		return create(classZ, null);
	}
	
	public static <T> T create(Class<T> classZ,MockConfig config){
		return null;
	}

}
