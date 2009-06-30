package org.nutz.java;

import org.nutz.java.JavaLanguageObject;

public abstract class JavaStatement extends JavaLanguageObject {

	@Override
	public String toString() {
		return this.renderSource();
	}

	
	
}
