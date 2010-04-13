package org.nutz.mvc.upload.performance;

import static java.lang.System.out;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.nutz.filepool.FilePool;
import org.nutz.filepool.NutFilePool;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Stopwatch;
import org.nutz.mvc.upload.MockHttpRequest;
import org.nutz.mvc.upload.MockProperties;
import org.nutz.mvc.upload.MultiFileContent;
import org.nutz.mvc.upload.MultipartBody;

public class UploadTest {

	public static void main(String[] args) throws IOException {

		Properties pros = new Properties();
		File file = Files
				.findFile("org/nutz/mvc/upload/performance/performance.properties");
		pros.load(new FileInputStream(file));
		String contentType = pros.getProperty("Content-Type") + " boundary="
				+ pros.getProperty("boundary");
		MultipartBody mb = new MultipartBody(contentType, pros
				.getProperty("boundary"));
		String str = pros.getProperty("srcFile");
		File srcFile = Files.findFile(str);

		Integer buffer = MockProperties.getBufferIn();
		MultiFileContent mfc = new MultiFileContent("fileData", srcFile, buffer);
		mb.addMultiFileContent(mfc);
		out.printf("\t[buffer.in:%d] - %s\n", buffer, srcFile.getName());
		mb.prepareForRead();
		// System.out.println(readMultipartBody(mb));
		MockHttpRequest request = new MockHttpRequest(mb);
		Stopwatch sw = Stopwatch.begin();
		MockUploading up = new MockUploading(request);
		File destFile = new File(pros.getProperty("destFile"));
		destFile.delete();
		destFile.createNewFile();
		up.parse(request, destFile);
		sw.stop();
		out.println(sw.toString());

	}

}
