package org.nutz.ioc.aop;

import org.nutz.aop.ClassAgent;

public class AopObject {

	private ObjectMatcher objectMatcher;

	private ClassAgent classAgent;

	AopObject(ObjectMatcher objectMatcher, ClassAgent classAgent) {
		this.objectMatcher = objectMatcher;
		this.classAgent = classAgent;
	}

	public ObjectMatcher getObjectMatcher() {
		return objectMatcher;
	}

	public ClassAgent getClassAgent() {
		return classAgent;
	}

}
