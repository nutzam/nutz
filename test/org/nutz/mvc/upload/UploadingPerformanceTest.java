package org.nutz.mvc.upload;

import static java.lang.System.err;
import static java.lang.System.out;
import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;
import org.nutz.filepool.FilePool;
import org.nutz.filepool.NutFilePool;
import org.nutz.lang.Files;
import org.nutz.lang.Stopwatch;
import org.nutz.lang.Streams;

/**
 * test uploading's performance
 * 
 * @author amosleaf
 */
public class UploadingPerformanceTest {

	public static void main(String[] args) throws IOException, UploadFailException {

		if (args.length != 1) {
			err.println("You need give a path for mock.properties");
			System.exit(0);
		}

		File f = Files.findFile(args[0]);
		if (null == f) {
			err.println("Fail to find :" + args[0]);
			System.exit(0);
		}

		Properties p = new Properties();
		p.load(Streams.fileIn(f));
		MockProperties.setMockProperties(p);

		out.println("Begin ...");
		new UploadingPerformanceTest().testParsepeed();
		out.println("... Done!");
	}

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
		File file = Files.findFile("哈哈\\abc.txt");
		MultiFileHead mfh = new MultiFileHead("fileData", file.getPath());
		String except = "Content-Disposition: form-data; name=\"fileData\"; filename=\""
						+ file.getPath()
						+ "\"\r\nContent-Type: text/plain\r\n\r\n";
		String tr = this.readMultiReadable(mfh);
		assertEquals(except, tr);
	}

	@Test
	public void testMultiFileContent() throws Exception {
		File file1 = Files.findFile("org/nutz/mvc/upload/upload.txt");
		MultiFileContent mfc1 = new MultiFileContent(file1.getName(), file1, -1);
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
		String contentType = " "
								+ pros.getProperty("Content-Type")
								+ " boundary="
								+ pros.getProperty("boundary");
		MultipartBody mb = new MultipartBody(contentType, pros.getProperty("boundary"));
		File file = Files.findFile("org/nutz/mvc/upload/upload.txt");
		MultiFileContent mfc = new MultiFileContent("fileData", file, -1);
		mb.addMultiFileContent(mfc);
		mb.prepareForRead();
		readMultipartBody(mb);
	}

	@Test
	public void testMockServletInputStream_Read() throws IOException {
		Properties pros = MockProperties.getMockProperties();
		String contentType = " "
								+ pros.getProperty("Content-Type")
								+ " boundary="
								+ pros.getProperty("boundary");
		MultipartBody mb = new MultipartBody(contentType, pros.getProperty("boundary"));
		File file = Files.findFile("org/nutz/mvc/upload/upload.txt");
		MultiFileContent mfc = new MultiFileContent("fileData", file, -1);
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
		String contentType = " "
								+ pros.getProperty("Content-Type")
								+ " boundary="
								+ pros.getProperty("boundary");
		MultipartBody mb = new MultipartBody(contentType, pros.getProperty("boundary"));
		File file = Files.findFile("org/nutz/mvc/upload/upload.txt");
		MultiFileContent mfc = new MultiFileContent("fileData", file, -1);
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
	public void testParsepeed() throws UploadFailException, IOException {
		Properties pros = MockProperties.getMockProperties();
		String contentType = pros.getProperty("Content-Type")
								+ " boundary="
								+ pros.getProperty("boundary");
		MultipartBody mb = new MultipartBody(contentType, pros.getProperty("boundary"));
		String str = pros.getProperty("files");
		String[] files = str.split(",");
		int i = 0;
		for (String f : files) {
			addFiles(mb, Files.findFile(f), i);
		}
		mb.prepareForRead();
		// System.out.println(readMultipartBody(mb));
		MockHttpRequest request = new MockHttpRequest(mb);
		String charset = pros.getProperty("charset");
		String poolPath = pros.getProperty("tmp");
		File pool = new File(poolPath);
		pool.delete();
		pool.mkdirs();

		Stopwatch sw = Stopwatch.begin();
		out.println("Check temp file pool");
		FilePool tmpFiles = new NutFilePool(pool.getAbsolutePath(), 500);

		int buffer = 0;
		try {
			buffer = Integer.parseInt(pros.getProperty("buffer.upload"));
		}
		catch (NumberFormatException e) {}
		out.println("buffer.upload: " + buffer);

		Uploading up = new SimpleUploading(buffer);
		Map<String, Object> params = up.parse(request, charset, tmpFiles);
		sw.stop();
		out.println(sw.toString());
		out.printf("find %d params", params.size());
		for (String key : params.keySet()) {
			out.printf("\t@%s : %s\n", key, params.get(key).getClass().getSimpleName());
		}
	}

	private static void addFiles(MultipartBody mb, File file, int index) {
		if (file.isFile()) {
			Integer buffer = MockProperties.getBufferIn();
			MultiFileContent mfc = new MultiFileContent("fileData" + String.valueOf(index++),
														file,
														buffer);
			mb.addMultiFileContent(mfc);
			out.printf("\t[buffer.in:%d] - %s\n", buffer, file.getName());
			return;
		} else if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				addFiles(mb, f, index);
			}
		}
	}

}
