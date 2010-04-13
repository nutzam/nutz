package org.nutz.mvc.upload;

import java.io.IOException;
import java.io.InputStream;

public class UploadInfo {

	public static final String SESSION_NAME = "UPLOAD_INFO";

	public int sum;

	public int current;

	public int read(InputStream ins) throws IOException {
		current++;
		return ins.read();
	}
}
