package org.nutz.mvc.upload.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.ServletInputStream;

import org.junit.Test;
import org.nutz.filepool.FilePool;
import org.nutz.filepool.NutFilePool;
import org.nutz.lang.Files;
import org.nutz.lang.Streams;
import org.nutz.mock.Mock;
import org.nutz.mock.servlet.MockHttpServletRequest;
import org.nutz.mock.servlet.multipart.MultipartInputStream;
import org.nutz.mvc.upload.FastUploading;
import org.nutz.mvc.upload.SimpleUploading;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadException;
import org.nutz.mvc.upload.UploadUnit;
import org.nutz.mvc.upload.Uploading;

public class UploadingUnitTest {

	@Test
	public void test_upload_1txt_3img() throws UploadException {
		MockHttpServletRequest req = Mock.servlet.request();
		req.setPathInfo("/nutz/junit/uploading");
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

		Uploading up = UploadUnit.TYPE.born(8192);
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

	@Test
	public void test_upload_queryString() throws UploadException {
		MockHttpServletRequest req = Mock.servlet.request();
		req.setQueryString("id=1&name=nutz");
		FilePool tmps = new NutFilePool("~/nutz/junit/uploadtmp");
		Uploading up = UploadUnit.TYPE.born(8192);
		MultipartInputStream ins = Mock.servlet.insmulti();
		ins.append("age", "1");
		req.setInputStream(ins);
		req.init();
		Map<String, Object> map = up.parse(req, "UTF-8", tmps);
		assertEquals("1", map.get("id"));
		assertEquals("nutz", map.get("name"));
		assertEquals("1", map.get("age"));
		assertEquals(null, map.get("null"));
	}

	@Test
	public void test_upload_onlyQueryString() throws UploadException {
		MockHttpServletRequest req = Mock.servlet.request();
		req.setQueryString("id=1&name=nutz");
		FilePool tmps = new NutFilePool("~/nutz/junit/uploadtmp");
		Uploading up = UploadUnit.TYPE.born(8192);
		MultipartInputStream ins = Mock.servlet.insmulti();
		req.setInputStream(ins);
		req.init();
		Map<String, Object> map = up.parse(req, "UTF-8", tmps);
		assertEquals("1", map.get("id"));
		assertEquals("nutz", map.get("name"));
		assertEquals(null, map.get("null"));
	}

	@Test
	public void test_cast_dt01() throws UploadException {
		MockHttpServletRequest req = Mock.servlet.request();
		req.setHeader(	"content-type",
						"multipart/form-data; boundary=----ESDT-321271401654cc6d669eef664aac");
		FilePool tmps = new NutFilePool("~/nutz/junit/uploadtmp");
		Uploading up = UploadUnit.TYPE.born(8192);
		ServletInputStream ins = Mock.servlet.ins("org/nutz/mvc/upload/files/cast_dt01");
		req.setInputStream(ins);
		req.init();
		Map<String, Object> map = up.parse(req, "UTF-8", tmps);
		assertEquals(1, map.size());
		assertEquals("Shapes100.jpg", ((TempFile) map.get("fileData")).getMeta().getFileLocalPath());
	}
}
