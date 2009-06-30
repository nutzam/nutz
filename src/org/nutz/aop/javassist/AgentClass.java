package org.nutz.aop.javassist;

public class AgentClass {

	private static final String SUFFIX = "$$NUTAOP";
	
	private String newName;
	private String oldName;

	public AgentClass(Class<?> klass) {
		this.oldName = klass.getName();
		this.newName = klass.getName() + SUFFIX;
	}

	public String getOldName() {
		return oldName;
	}

	public String getNewName() {
		return newName;
	}

}
