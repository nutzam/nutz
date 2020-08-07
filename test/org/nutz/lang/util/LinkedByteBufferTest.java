package org.nutz.lang.util;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;
import org.nutz.lang.Lang;

public class LinkedByteBufferTest {

    @Test
    public void test_read_empty() throws IOException {
        LinkedByteBuffer lba = new LinkedByteBuffer(3, 1);
        byte[] bs = new byte[100];
        int len = lba.read(bs);
        assertEquals(-1, len);
    }

    @Test
    public void test_str_get_set() throws IOException {
        LinkedByteBuffer lba = new LinkedByteBuffer(3, 1);
        lba.write("1234567890");

        assertEquals('1', lba.get(0));
        assertEquals('2', lba.get(1));
        assertEquals('3', lba.get(2));
        assertEquals('4', lba.get(3));
        assertEquals('5', lba.get(4));
        assertEquals('6', lba.get(5));
        assertEquals('7', lba.get(6));
        assertEquals('8', lba.get(7));
        assertEquals('9', lba.get(8));
        assertEquals('0', lba.get(9));

        lba.set(0, 'a');
        lba.set(3, 'b');
        lba.set(9, 'c');

        assertEquals('a', lba.get(0));
        assertEquals('2', lba.get(1));
        assertEquals('3', lba.get(2));
        assertEquals('b', lba.get(3));
        assertEquals('5', lba.get(4));
        assertEquals('6', lba.get(5));
        assertEquals('7', lba.get(6));
        assertEquals('8', lba.get(7));
        assertEquals('9', lba.get(8));
        assertEquals('c', lba.get(9));

        lba.set(-1, 'x');
        lba.set(-2, 'y');
        lba.set(-3, 'z');

        assertEquals('a', lba.get(0));
        assertEquals('2', lba.get(1));
        assertEquals('3', lba.get(2));
        assertEquals('b', lba.get(3));
        assertEquals('5', lba.get(4));
        assertEquals('6', lba.get(5));
        assertEquals('7', lba.get(6));
        assertEquals('z', lba.get(7));
        assertEquals('y', lba.get(8));
        assertEquals('x', lba.get(9));

        String str = lba.readAll();
        assertEquals("a23b567zyx", str);
        assertNull(lba.readAll());
    }

    @Test
    public void test_str_seek_read_write2() throws IOException {
        LinkedByteBuffer lba = new LinkedByteBuffer(3, 1);
        lba.write("1234567890");

        String str = lba.readAll();
        assertEquals("1234567890", str);

        lba.seekRead(3);
        str = lba.readAll();
        assertEquals("4567890", str);

        assertNull(lba.readAll());
        assertEquals(10, lba.getLimit());
        assertEquals(10, lba.getWriteIndex());

        lba.seekWrite(2);
        lba.write("abc");
        lba.seekRead(0);
        lba.seekWrite(10);

        str = lba.readAll();
        assertEquals("12abc67890", str);
    }

    @Test
    public void test_str_seek_read_write() throws IOException {
        LinkedByteBuffer lba = new LinkedByteBuffer(3, 1);
        lba.write("123456789");

        String str = lba.readAll();
        assertEquals("123456789", str);

        lba.seekRead(3);
        str = lba.readAll();
        assertEquals("456789", str);

        assertNull(lba.readAll());
        assertEquals(9, lba.getLimit());
        assertEquals(9, lba.getWriteIndex());

        lba.seekWrite(2);
        lba.write("abc");
        lba.seekRead(0);
        lba.seekWrite(9);

        str = lba.readAll();
        assertEquals("12abc6789", str);
    }

    @Test
    public void test_str_read_write() throws IOException {
        LinkedByteBuffer lba = new LinkedByteBuffer(3, 1);
        lba.write("1234567890");

        String str = lba.readAll();
        assertEquals("1234567890", str);

        assertNull(lba.readAll());
    }

    @Test
    public void test_str_write_readAll() throws IOException {
        LinkedByteBuffer lba = new LinkedByteBuffer(3, 1);
        lba.write("hello");
        lba.write(" world");

        String str = lba.readAll();
        assertEquals("hello world", str);

        String sha1 = Lang.sha1(str);
        assertEquals(sha1, lba.sha1sum());

        assertNull(lba.readAll());
        assertNull(lba.readAll());
        assertNull(lba.readLine());
        assertNull(lba.readLine());
    }

    @Test
    public void test_str_write_readLine() throws IOException {
        LinkedByteBuffer lba = new LinkedByteBuffer(3, 1);
        lba.write("hello");
        lba.write(" world");

        String str = lba.readLine();
        assertEquals("hello world", str);

        String md5 = Lang.md5(str);
        assertEquals(md5, lba.md5sum());

        assertNull(lba.readLine());
        assertNull(lba.readLine());
        assertNull(lba.readAll());
        assertNull(lba.readAll());
    }

}
