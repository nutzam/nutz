package org.nutz.dao.test.normal;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.sql.SQLException;

import org.junit.Test;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.BinObject;
import org.nutz.dao.test.meta.TheGoods;
import org.nutz.dao.util.blob.SimpleClob;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
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
    
    @Test
    public void test_blob() throws IOException {
        dao.create(BinObject.class, true);
        
        BinObject obj = new BinObject();
        obj.setXblob(new ByteArrayInputStream("中文".getBytes()));
        obj.setXclob(new StringReader("不是英文"));
        dao.insert(obj);
        
        BinObject db_obj = dao.fetch(BinObject.class);
        assertTrue(Streams.equals(new ByteArrayInputStream("中文".getBytes()), db_obj.getXblob()));
        assertEquals("不是英文", Lang.readAll(db_obj.getXclob()));
    }
    
    //for issue 278
    @Test
    public void test_clob() throws IOException, SQLException {
        dao.create(BinObject.class, true);
        BinObject bin = new BinObject();
        File f = File.createTempFile("clob", "data");
        Files.write(f, "中文");
        bin.setMyClob(new SimpleClob(f));
        dao.insert(bin);
        
        bin = dao.fetch(BinObject.class);
        String str = Lang.readAll(bin.getMyClob().getCharacterStream());
        assertEquals("中文", str);
    }
}













