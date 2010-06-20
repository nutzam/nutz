package org.nutz.mvc.upload.unit;

import static org.junit.Assert.*;
import static org.nutz.mock.Mock.servlet.context;
import static org.nutz.mock.Mock.servlet.ins;
import static org.nutz.mock.Mock.servlet.request;
import static org.nutz.mock.Mock.servlet.session;

import java.io.File;
import java.util.Map;

import javax.servlet.ServletInputStream;

import org.junit.Before;
import org.junit.Test;
import org.nutz.filepool.FilePool;
import org.nutz.filepool.NutFilePool;
import org.nutz.lang.Files;
import org.nutz.lang.Streams;
import org.nutz.mock.Mock;
import org.nutz.mock.servlet.MockHttpServletRequest;
import org.nutz.mock.servlet.multipart.MultipartInputStream;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadException;
import org.nutz.mvc.upload.UploadUnit;
import org.nutz.mvc.upload.Uploading;

public class UploadingUnitTest {

	private static final String charset = "UTF-8";

	private FilePool tmps;

	@Before
	public void before() {
		/*
		 * 准备临时文件池
		 */
		tmps = new NutFilePool("~/nutz/junit/uploadtmp");
	}

	/**
	 * 检查一下普通的非文件数据项是否能被正确解码
	 */
	@Test
	public void test_upload_multi_item_in_GBK() throws UploadException {
		/*
		 * 准备请求对象
		 */
		MockHttpServletRequest req = Mock.servlet.request();
		MultipartInputStream ins = Mock.servlet.insmulti("GBK");
		ins.append("abc", "程序员s");
		req.setInputStream(ins);
		req.init();
		/*
		 * 执行上传
		 */
		Uploading up = UploadUnit.TYPE.born(8192);
		Map<String, Object> map = up.parse(req, "GBK", tmps);
		/*
		 * 检查以下是不是 GBK 编码被解析成功
		 */
		assertEquals("程序员s", map.get("abc"));
	}

	/**
	 * @author lAndRaxeE(landraxee@gmail.com)
	 */
	@Test
	public void test_upload_chinese_filename() throws UploadException {
		/*
		 * 准备模拟对象
		 */
		MockHttpServletRequest req = Mock.servlet.request();
		File txt = Files.findFile("org/nutz/mvc/upload/files/quick/中文.txt");

		/*
		 * 如果模拟上传时request使用 GBK 编码，用 GBK 来解码，应该会生成正确的文件名
		 */
		req.setInputStream(Mock.servlet.insmulti("GBK", txt)).init();
		Uploading up = UploadUnit.TYPE.born(8192);
		TempFile txt2 = (TempFile) up.parse(req, "GBK", tmps).get("F0");
		// 测试本地的默认编码是否是GBK，即模拟中文环境，本人环境为中文Windows XP
		// 在JVM参数中增加-Dfile.encoding=GBK即可设置好
		// assertEquals("GBK", Charset.defaultCharset().name());
		// 
		// zzh: JUnit 测试必须在多数常用环境下可以比较方便的测试通过，经过这次修改，相信
		// 即可以达到这个目的，又可以测试出中文文件名的编码问题。如果没有其他的问题，在
		// 1.a.30 发布前，这段注释将被删除
		assertEquals("中文.txt", txt2.getMeta().getFileLocalName());

		/*
		 * 为了验证上传是否是真的可以解码，再次准备模拟 GBK 的输入流，但是这次将用 UTF-8 来解码
		 */
		req.setInputStream(Mock.servlet.insmulti("GBK", txt)).init();
		up = UploadUnit.TYPE.born(8192);
		txt2 = (TempFile) up.parse(req, "UTF-8", tmps).get("F0");
		assertFalse("中文.txt".equals(txt2.getMeta().getFileLocalName()));
	}

	@Test
	public void test_upload_1txt_3img() throws UploadException {
		MockHttpServletRequest req = Mock.servlet.request();
		req.setPathInfo("/nutz/junit/uploading");
		File txt = Files.findFile("org/nutz/mvc/upload/files/quick/abc.zdoc");
		File red = Files.findFile("org/nutz/mvc/upload/files/quick/red.png");
		File blue = Files.findFile("org/nutz/mvc/upload/files/quick/blue.png");
		File green = Files.findFile("org/nutz/mvc/upload/files/quick/green.png");

		MultipartInputStream ins = Mock.servlet.insmulti(charset);
		ins.append("abc", txt);
		ins.append("red", red);
		ins.append("blue", blue);
		ins.append("green", green);
		req.setInputStream(ins);
		req.init();

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
		Uploading up = UploadUnit.TYPE.born(8192);
		MultipartInputStream ins = Mock.servlet.insmulti(charset);
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
		Uploading up = UploadUnit.TYPE.born(8192);
		MultipartInputStream ins = Mock.servlet.insmulti(charset);
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
		Uploading up = UploadUnit.TYPE.born(8192);
		ServletInputStream ins = Mock.servlet.ins("org/nutz/mvc/upload/files/cast_dt01");
		req.setInputStream(ins);
		req.init();
		Map<String, Object> map = up.parse(req, "UTF-8", tmps);
		assertEquals(1, map.size());
		assertEquals("Shapes100.jpg", ((TempFile) map.get("fileData")).getMeta().getFileLocalPath());
	}

	@Test
	public void test_upload_text_with_newline_ending() throws UploadException {
		MockHttpServletRequest req = request()	.setInputStream(ins(Streams.fileIn("org/nutz/mvc/upload/unit/plaint.s")));
		req.setHeader(	"content-type",
						"multipart/form-data; boundary=------NutzMockHTTPBoundary@129021a3e21");
		req.setHeader("content-length", "200");
		req.setSession(session(context()));
		req.init();

		Uploading up = UploadUnit.TYPE.born(8192);
		up.parse(req, "UTF-8", tmps);
	}
}
