package org.nutz.mvc.upload;

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;
import org.nutz.filepool.FilePool;
import org.nutz.filepool.NutFilePool;
import org.nutz.lang.Files;
import org.nutz.lang.Stopwatch;

public class UploadingTest {

	private String readMultiReadable(MultiReadable mr) throws Exception {
		byte[] b = new byte[(int) mr.length()];
		int i = -1, j = 0;
		while ((i = mr.read()) != -1) {
			b[j++] = (byte) i;
		}
		return new String(b);
	}

	@Test
	public void testMultiFileHead() throws Exception {
		MultiFileHead mfh = new MultiFileHead("fileData", "D:\\a.txt");
		String except = "Content-Disposition: form-data; name=\"fileData\"; filename=\"D:\\a.txt\"\r\nContent-Type: text/plain\r\n\r\n";
		String tr = this.readMultiReadable(mfh);
		assertEquals(except, tr);
	}

	@Test
	public void testMultiFileContent() throws Exception {
		File file1 = Files.findFile("org/nutz/mvc/upload/upload.txt");
		MultiFileContent mfc1 = new MultiFileContent(file1.getName(), file1);
		assertEquals("Hello nutz!", this.readMultiReadable(mfc1));
	}

	@Test
	public void testMultiPlainContent() throws Exception {
		MultiPlainContent mpc = new MultiPlainContent("name", "nutz");
		String except = "Content-Disposition: form-data; name=\"name\"\r\n\r\nnutz";
		assertEquals(except, this.readMultiReadable(mpc));
	}

	@Test
	public void testMultiSeparator() throws Exception {
		MultiSeparator ms = new MultiSeparator();
		String except = "\r\n--"
				+ MockProperties.getMockProperties().getProperty("boundary")
				+ "\r\n";
		String real = this.readMultiReadable(ms);
		assertEquals(except, real);
	}

	@Test
	public void testMultiEnd() throws Exception {
		MultiEnd me = new MultiEnd();
		String except = "\r\n--"
				+ MockProperties.getMockProperties().getProperty("boundary")
				+ "--\r\n";
		String real = this.readMultiReadable(me);
		assertEquals(except, real);
	}

	private String readMultipartBody(MultipartBody mb) throws Exception {
		byte[] b = new byte[(int) mb.getContentLength()];
		int i = -1, j = 0;
		while ((i = mb.read()) != -1) {
			b[j++] = (byte) i;
		}
		return new String(b);
	}

	@Test
	public void testMultipartBody() throws Exception {
		Properties pros = MockProperties.getMockProperties();
		String contentType = " " + pros.getProperty("Content-Type")
				+ " boundary=" + pros.getProperty("boundary");
		MultipartBody mb = new MultipartBody(contentType, pros
				.getProperty("boundary"));
		File file = Files.findFile("org/nutz/mvc/upload/upload.txt");
		MultiFileContent mfc = new MultiFileContent("fileData", file);
		mb.addMultiFileContent(mfc);
		readMultipartBody(mb);
	}

	@Test
	public void testMockServletInputStream_Read() throws IOException {
		Properties pros = MockProperties.getMockProperties();
		String contentType = " " + pros.getProperty("Content-Type")
				+ " boundary=" + pros.getProperty("boundary");
		MultipartBody mb = new MultipartBody(contentType, pros
				.getProperty("boundary"));
		File file = Files.findFile("org/nutz/mvc/upload/upload.txt");
		MultiFileContent mfc = new MultiFileContent("fileData", file);
		mb.addMultiFileContent(mfc);
		MockHttpRequest request = new MockHttpRequest(mb);
		InputStream ins = request.getInputStream();
		StringBuilder sb = new StringBuilder();
		int i = -1;
		while ((i = ins.read()) != -1) {
			sb.append((char) i);
		}
	}

	@Test
	public void testMockServletInputStream_ReadByte() throws IOException {
		Properties pros = MockProperties.getMockProperties();
		String contentType = " " + pros.getProperty("Content-Type")
				+ " boundary=" + pros.getProperty("boundary");
		MultipartBody mb = new MultipartBody(contentType, pros
				.getProperty("boundary"));
		File file = Files.findFile("org/nutz/mvc/upload/upload.txt");
		MultiFileContent mfc = new MultiFileContent("fileData", file);
		mb.addMultiFileContent(mfc);
		MockHttpRequest request = new MockHttpRequest(mb);
		InputStream ins = request.getInputStream();
		BufferedInputStream bis = new BufferedInputStream(ins);
		byte[] b = new byte[1024];
		StringBuilder sb = new StringBuilder();
		int len;
		while ((len = bis.read(b)) != -1) {
			for (int i = 0; i < len; i++) {
				sb.append((char) b[i]);
			}
		}
		sb.toString();
	}

	@Test
	public void testParsepeed() throws Exception {
		Properties pros = MockProperties.getMockProperties();
		String contentType = pros.getProperty("Content-Type") + " boundary="
				+ pros.getProperty("boundary");
		MultipartBody mb = new MultipartBody(contentType, pros
				.getProperty("boundary"));
		String str = pros.getProperty("files");
		String[] files = str.split(",");
		int i = 0;
		for (String f : files) {
			MultiFileContent mfc = new MultiFileContent("fileData"
					+ String.valueOf(i++), new File(f));
			mb.addMultiFileContent(mfc);
		}
		// System.out.println(readMultipartBody(mb));
		MockHttpRequest request = new MockHttpRequest(mb);
		String charset = pros.getProperty("charset");
		String poolPath = "org/nutz/mvc/upload/NutFilePool";
		File pool = new File(poolPath);
		pool.delete();
		pool.mkdirs();

		Stopwatch sw = Stopwatch.begin();
		FilePool tmpFiles = new NutFilePool(pool.getAbsolutePath(), 500);
		Uploading up = new Uploading();
		up.parse(request, charset, tmpFiles);
		Assert.assertTrue(Files.deleteDir(pool));
		sw.stop();
		System.out.println(String
				.format("Copy file use %sms", sw.getDuration()));
	}

}
