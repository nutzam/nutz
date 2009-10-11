package org.nutz.mvc2.param;

public class ParamBean {

	Class<?> type;
	private String name;

	ParamBean(Class<?> type, String name) {
		this.type = type;
		this.name = name;
	}

	public Class<?> getType() {
		return type;
	}

	public String getName() {
		return name;
	}

}
