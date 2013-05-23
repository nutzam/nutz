package org.nutz.lang.encrypt;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;

public class MsgDigestOutputStreamTest {

	@Test
	public void test_dis() throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		MsgDigestOutputStream out = new MsgDigestOutputStream(bout, "md5");
		out.write("abc".getBytes());
		Streams.safeClose(out);
		assertEquals(Lang.md5("abc"), out.digest());
	}
}
