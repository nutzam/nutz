package org.nutz.mvc.view;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Lang;
import org.nutz.lang.util.LinkedIntArray;

public class CachedDownloadView extends DownloadView {

	private byte[] bytes;
	private long lastModified;

	@Override
	public void render(HttpServletRequest req, HttpServletResponse resp, Object obj) {
		File f = prepareFile(req, resp);
		if (null == bytes || lastModified != f.lastModified()) {
			// Read bytes
			LinkedIntArray stack = new LinkedIntArray();
			try {
				InputStream ins = new BufferedInputStream(new FileInputStream(f));
				int b;
				while (-1 != (b = ins.read()))
					stack.push(b);
				bytes = new byte[stack.size()];
				for (int i = 0; i < bytes.length; i++)
					bytes[i] = (byte) stack.get(i);
				ins.close();
				stack = null;
			} catch (Exception e) {
				throw Lang.wrapThrow(e);
			}
			// update last modify
			lastModified = f.lastModified();
		}
		try {
			resp.getOutputStream().write(bytes);
			resp.flushBuffer();
		} catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
	}

}
