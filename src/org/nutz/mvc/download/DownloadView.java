package org.nutz.mvc.download;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.View;

public class DownloadView implements View {

	@Override
	public void render(HttpServletRequest request, HttpServletResponse response, Object obj)
			throws Exception {
		File f = (File) obj;
		// for firefox
		response.reset();
		response.setContentType("application/octet-stream;charset=UTF-8");
		String fileName = new String(f.getName().getBytes("UTF-8"),"iso-8859-1");
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
		// TODO implement filename for IE
		InputStream ins = new BufferedInputStream(new FileInputStream(f));
		ServletOutputStream ops = response.getOutputStream();
		int b;
		while ((b = ins.read()) != -1) {
			ops.write(b);
		}
		ins.close();
		ops.flush();
		ops.close();
	}

}
