package org.nutz.mvc.upload;

import java.io.Serializable;

public class UploadInfo implements Serializable {

	private static final long serialVersionUID = 1145316598297147074L;

	public static final String SESSION_NAME = "UPLOAD_INFO";

	public long sum;

	public long current;

}
