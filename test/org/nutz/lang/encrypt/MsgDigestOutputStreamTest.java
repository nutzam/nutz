package org.nutz.lang.encrypt;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;
import org.nutz.lang.Lang;

public class MsgDigestOutputStreamTest {

	@Test
	public void test_dis() throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		MsgDigestOutputStream out = new MsgDigestOutputStream(bout, "md5");
		out.write("abc".getBytes());
		assertEquals(Lang.md5("abc"), out.digest());
	}
}
