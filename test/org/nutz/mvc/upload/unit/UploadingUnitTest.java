package org.nutz.mvc.upload.unit;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Map;

import org.junit.Test;
import org.nutz.filepool.FilePool;
import org.nutz.filepool.NutFilePool;
import org.nutz.lang.Files;
import org.nutz.mock.Mock;
import org.nutz.mock.servlet.MockHttpServletRequest;
import org.nutz.mock.servlet.multipart.MultipartInputStream;
import org.nutz.mvc.upload.SimpleUploading;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadFailException;
import org.nutz.mvc.upload.Uploading;

public class UploadingUnitTest {

	@Test
	public void test_upload_1txt_3img() throws UploadFailException {
		MockHttpServletRequest req = Mock.servlet.request();
		File txt = Files.findFile("org/nutz/mvc/upload/files/quick/abc.zdoc");
		File red = Files.findFile("org/nutz/mvc/upload/files/quick/red.png");
		File blue = Files.findFile("org/nutz/mvc/upload/files/quick/blue.png");
		File green = Files.findFile("org/nutz/mvc/upload/files/quick/green.png");

		MultipartInputStream ins = Mock.servlet.insmulti();
		ins.append("abc", txt);
		ins.append("red", red);
		ins.append("blue", blue);
		ins.append("green", green);
		req.setInputStream(ins);
		req.init();

		FilePool tmps = new NutFilePool("~/nutz/junit/uploadtmp");

		Uploading up = new SimpleUploading(8192);
		Map<String, Object> map = up.parse(req, "UTF-8", tmps);
		assertEquals(4, map.size());
		TempFile txt2 = (TempFile) map.get("abc");
		TempFile red2 = (TempFile) map.get("red");
		TempFile blue2 = (TempFile) map.get("blue");
		TempFile green2 = (TempFile) map.get("green");

		assertEquals("abc.zdoc", txt2.getMeta().getFileLocalName());
		assertTrue(Files.equals(txt, txt2.getFile()));

		assertEquals("red.png", red2.getMeta().getFileLocalName());
		assertTrue(Files.equals(red, red2.getFile()));

		assertEquals("blue.png", blue2.getMeta().getFileLocalName());
		assertTrue(Files.equals(blue, blue2.getFile()));

		assertEquals("green.png", green2.getMeta().getFileLocalName());
		assertTrue(Files.equals(green, green2.getFile()));

	}

}
