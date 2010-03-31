package org.nutz.mvc.upload;

import java.io.IOException;

public interface MultiReadable {

	public int read() throws Exception;

	public long length();

	public void close() throws IOException;
}
