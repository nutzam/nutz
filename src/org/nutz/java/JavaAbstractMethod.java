package org.nutz.java;

public class JavaAbstractMethod extends JavaMethod {

	@Override
	protected String renderSource() {
		return this.createMethodHead().append(";").toString();
	}

}
