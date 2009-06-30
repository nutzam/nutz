package org.nutz.java;

import java.util.HashSet;
import java.util.Set;

public abstract class JavaLanguageObject {
	
	protected abstract String renderSource();
	
	public Set<JavaType> getDependents(){
		return new HashSet<JavaType>();
	}
	
}
