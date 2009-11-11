package org.nutz.ioc.meta;

import org.nutz.json.Json;

public class IocValue {

	private String type;

	private Object value;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return String.format("{%s:%s}", type, Json.toJson(value));
	}

}
