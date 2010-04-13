package org.nutz.mvc.upload;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.nutz.lang.Dumps;
import org.nutz.lang.Files;
import org.nutz.mock.Mock;
import org.nutz.mock.servlet.MockHttpServletRequest;
import org.nutz.mock.servlet.multipart.MultipartInputStream;

public class UploadingUnitTest {

	public static void main(String[] args) throws IOException {
		File dir = Files.findFile(args[0]);
		if (null == dir || !dir.isDirectory()) {
			System.out.println("Fail to find : " + args[0]);
			System.exit(0);
		}

		File of = Files.createFileIfNoExists(args[1]);

		/*
		 * Prepare HTTP Request
		 */
		MockHttpServletRequest req = Mock.servlet.request();
		MultipartInputStream ins = Mock.servlet.ins("------WebKitFormBoundaryJ1QzxGryuaxBZPTq");
		// Init params
		ins.append("pa", "abc");
		// load files
		ins.append("fileA", Files.findFile("/home/zozoh/tmp/upload/quick/abc.zdoc"));
		ins.append("fileB", Files.findFile("/home/zozoh/tmp/upload/quick/tickets-icon.png"));
		ins.append("fileC", Files.findFile("/home/zozoh/tmp/upload/quick/ttt.txt"));
		req.setInputStream(ins);
		req.init();

		/*
		 * Prepare Outputstream
		 */
		OutputStream ops = new BufferedOutputStream(new FileOutputStream(of));

		/*
		 * Do dumping
		 */
		Dumps.HTTP.all(req, ops);

		System.out.println("done!");

	}

}
