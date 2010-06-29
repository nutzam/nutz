package org.nutz.resource;

import java.io.IOException;
import java.io.InputStream;

public abstract class NutResource implements Comparable<NutResource> {

	protected String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public abstract InputStream getInputStream() throws IOException;

	public int compareTo(NutResource o) {
		if (null == name || null == o || null == o.getName())
			return -1;
		return name.compareTo(o.getName());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NutResource)
			return 0 == compareTo((NutResource) obj);
		return false;
	}

	@Override
	public int hashCode() {
		return null == name ? "NULL".hashCode() : name.hashCode();
	}

	@Override
	public String toString() {
		return name;
	}

}
