package com.zzh.lang.meta;

import com.zzh.lang.Strings;

public class Property {

	private String name;

	private String value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof Property)
			return Strings.equals(((Property) obj).name, name)
					&& Strings.equals(((Property) obj).value, value);
		return false;
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public String toString() {
		return String.format("%s=%s", name, value);
	}

}
