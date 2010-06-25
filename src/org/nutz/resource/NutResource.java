package org.nutz.resource;

import java.io.IOException;
import java.io.InputStream;

public abstract class NutResource {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public abstract InputStream getInputStream() throws IOException;

}
