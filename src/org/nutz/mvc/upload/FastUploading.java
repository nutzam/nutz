package org.nutz.mvc.upload;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.nutz.filepool.FilePool;

public class FastUploading implements Uploading {

	public Map<String, Object> parse(HttpServletRequest request, String charset, FilePool tmpFiles)
			throws UploadFailException {
		return null;
	}

}
