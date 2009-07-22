package org.nutz.ioc.aop;

public class ObjectHooking {

	private ObjectMatcher objectMatcher;

	private ObjectMethodHooking[] methodHookings;

	ObjectHooking(ObjectMatcher matcher, ObjectMethodHooking[] pairs) {
		this.objectMatcher = matcher;
		this.methodHookings = pairs;
	}

	public ObjectMatcher getObjectMatcher() {
		return objectMatcher;
	}

	public ObjectMethodHooking[] getMethodHookings() {
		return methodHookings;
	}

}
