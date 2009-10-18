package org.nutz.mvc.view;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Lang;
import org.nutz.mvc.View;

public class DownloadView implements View {

	protected File prepareFile(HttpServletRequest req, HttpServletResponse resp) {
		File f = (File) req.getAttribute("mime");
		resp.setContentType("application/octet-stream;charset=UTF-8");
		try {
			String fileName = new String(f.getName().getBytes("UTF-8"), "iso-8859-1");
			resp.setHeader("Content-Disposition", "attachment; filename=" + fileName);
		} catch (UnsupportedEncodingException e) {}
		return f;
	}

	/**
	 * It should support download range
	 */
	public void render(HttpServletRequest req, HttpServletResponse resp, Object obj) {
		File f = prepareFile(req, resp);
		try {
			OutputStream ops = new BufferedOutputStream(resp.getOutputStream());
			InputStream ins = new BufferedInputStream(new FileInputStream(f));
			int b;
			while (-1 != (b = ins.read()))
				ops.write(b);
			try {
				ins.close();
			} catch (IOException e) {}
			try {
				ops.flush();
				ops.close();
			} catch (IOException e) {}
		} catch (FileNotFoundException e) {
			throw Lang.wrapThrow(e);
		} catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
	}

}
