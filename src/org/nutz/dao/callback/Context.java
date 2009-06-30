package org.nutz.dao.callback;

import org.nutz.dao.FieldMatcher;

public class Context {

	private FieldMatcher fieldsMatcher;

	public FieldMatcher getFieldsMatcher() {
		return fieldsMatcher;
	}

	public void setFieldsMatcher(FieldMatcher actived) {
		this.fieldsMatcher = actived;
	}
	
}
