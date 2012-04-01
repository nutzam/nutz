package org.nutz.dao.test.normal;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.Test;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.TheGoods;
import org.nutz.lang.Files;
import org.nutz.lang.Streams;

public class BinaryDaoTest extends DaoCase {

	/**
	 * @see Issue #119 为何从数据库里面取出二进制数据时会被转成String类型？
	 */
	@Test
	public void test_simple_byte_array() throws IOException {
		String path = "org/nutz/dao/test/meta/goods.png";

		dao.create(TheGoods.class, true);
		TheGoods tg = TheGoods.create("AAA", path);
		dao.insert(tg);

		// 读取
		TheGoods tg2 = dao.fetch(TheGoods.class, tg.getId());

		// 比较字节流
		byte[] olds = Files.readBytes(path);
		byte[] dbs = tg2.getThumbnail();

		assertEquals(olds.length, dbs.length);
		for (int i = 0; i < olds.length; i++) {
			assertEquals(olds[i], dbs[i]);
		}
	}

	@Test
	public void test_big_blob() throws IOException {
		String path = "~/tmp/big.blob";
		Files.createFileIfNoExists(path);
		OutputStream fos = Streams.fileOut(path);
		for (int i = 0; i < 10240; i++) {
			fos.write(new byte[1024]);
		}
		fos.close();

		dao.create(TheGoods.class, true);
		TheGoods tg = TheGoods.create("AAA", path);
		dao.insert(tg);
		
		new File(path).delete();
	}
}
