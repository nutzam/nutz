package org.nutz.resource;

import java.io.IOException;
import java.io.InputStream;

public abstract class NutResource {

	protected String name;

	public String getName() {
		return name;
	}

	public abstract InputStream getInputStream() throws IOException;

}
