package org.nutz.mvc.upload;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.nutz.filepool.FilePool;
import org.nutz.lang.Lang;
import org.nutz.lang.util.LinkedArray;
import org.nutz.lang.util.LinkedIntArray;

public class FastUploading implements Uploading {

	private int bufferSize;

	public FastUploading(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public Map<String, Object> parse(HttpServletRequest req, String charset, FilePool tmps)
			throws UploadFailException {

		LinkedIntArray buffer = new LinkedIntArray(bufferSize);
		
		
		try {
			ServletInputStream ins = req.getInputStream();
		}
		catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
		return null;
	}

}
