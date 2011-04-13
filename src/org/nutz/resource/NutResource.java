package org.nutz.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.nutz.lang.Streams;

public abstract class NutResource implements Comparable<NutResource> {

	protected String name;

	public String getName() {
		return name;
	}

	public NutResource setName(String name) {
		this.name = name;
		return this;
	}

	public abstract InputStream getInputStream() throws IOException;

	public Reader getReader() throws IOException {
		return Streams.utf8r(getInputStream());
	}

	public int compareTo(NutResource o) {
		if (o == null)
			return -1;
		if (this == o || (this.name == null && o.name == null))
			return 0;
		if (this.name != null && o.name != null)
			return name.compareTo(o.getName());
		return this.name == null ? 1 : -1;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
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
		return String.format("NutResource[%s]", name);
	}

}
