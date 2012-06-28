package org.nutz.mvc.upload.util;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.nutz.lang.Lang;

public class BufferRingTest {

    @Test
    public void test_cross_ring_item() throws IOException {
        RemountBytes boundary = RemountBytes.create("---");
        String str = "ABCDE";
        str += "abcde";
        str += "1234-";
        str += "-1234";
        InputStream ins = Lang.ins(str);
        BufferRing br = new BufferRing(ins, 3, 5);
        String s;
        MarkMode mode;

        br.load();
        mode = br.mark(boundary);
        assertEquals(MarkMode.NOT_FOUND, mode);
        s = br.dumpAsString();
        assertEquals("ABCDEabcde1234", s);

        br.load();
        mode = br.mark(boundary);
        assertEquals(MarkMode.STREAM_END, mode);
        s = br.dumpAsString();
        assertEquals("--1234", s);

    }

    @Test
    public void test_normal_read() throws IOException {
        RemountBytes boundary = RemountBytes.create("---");
        String str = "12345";
        str += "67890";
        str += "ABCDE";
        str += "fgh--";
        str += "-A---";
        str += "B----";
        str += "--ENDL";
        str += "--";
        InputStream ins = Lang.ins(str);
        BufferRing br = new BufferRing(ins, 3, 5);
        String s;
        MarkMode mode;
        RingItem ri;

        /**
         * =================================================<br>
         * 12345 67890 ABCDE
         */
        br.load();
        assertEquals(15, br.readed);
        mode = br.mark(boundary);
        assertEquals(MarkMode.NOT_FOUND, mode);
        s = br.dumpAsString();
        assertEquals('1', (char) br.item.buffer[0]);
        assertTrue(br.item.isDone4Mark());
        assertFalse(br.item.isLoaded);
        assertEquals("1234567890ABCDE", s);

        /**
         * =================================================<br>
         * fgh-- -A--- B----
         */
        br.load();
        assertEquals(30, br.readed);
        mode = br.mark(boundary);
        assertEquals(MarkMode.FOUND, mode);
        // ITEM
        ri = br.item;
        assertTrue(ri.isLoaded);
        assertEquals(0, ri.l);
        assertEquals(3, ri.r);
        assertEquals(5, ri.nextmark);
        assertTrue(ri.isDone4Mark());
        // ITEM.NEXT
        ri = br.item.next;
        assertTrue(ri.isLoaded);
        assertEquals(1, ri.l);
        assertEquals(1, ri.r);
        assertEquals(1, ri.nextmark);
        assertFalse(ri.isDone4Mark());
        // ITEM.NEXT.NEXT
        ri = br.item.next.next;
        assertTrue(ri.isLoaded);
        assertEquals(0, ri.l);
        assertEquals(0, ri.r);
        assertEquals(0, ri.nextmark);
        assertFalse(ri.isDone4Mark());

        s = br.dumpAsString();
        assertEquals("fgh", s);

        /**
         * =================================================<br>
         * -A--- B----
         */
        br.load();
        assertEquals(30, br.readed);
        mode = br.mark(boundary);
        assertEquals(MarkMode.FOUND, mode);
        // ITEM
        ri = br.item;
        assertTrue(ri.isLoaded);
        assertEquals(1, ri.l);
        assertEquals(2, ri.r);
        assertEquals(5, ri.nextmark);
        assertTrue(ri.isDone4Mark());
        // ITEM.NEXT
        ri = br.item.next;
        assertTrue(ri.isLoaded);
        assertEquals(0, ri.l);
        assertEquals(0, ri.r);
        assertEquals(0, ri.nextmark);
        assertFalse(ri.isDone4Mark());
        // ITEM.NEXT.NEXT
        ri = br.item.next.next;
        assertFalse(ri.isLoaded);

        s = br.dumpAsString();
        assertEquals("A", s);

        /**
         * =================================================<br>
         * B----
         */
        br.load();
        assertEquals(30, br.readed);
        mode = br.mark(boundary);
        assertEquals(MarkMode.FOUND, mode);
        // ITEM
        ri = br.item;
        assertTrue(ri.isLoaded);
        assertEquals(0, ri.l);
        assertEquals(1, ri.r);
        assertEquals(4, ri.nextmark);
        assertFalse(ri.isDone4Mark());
        // ITEM.NEXT
        ri = br.item.next;
        assertFalse(ri.isLoaded);
        // ITEM.NEXT.NEXT
        ri = br.item.next.next;
        assertFalse(ri.isLoaded);

        s = br.dumpAsString();
        assertEquals("B", s);
        ri = br.item;
        assertTrue(ri.isLoaded);
        assertEquals(4, ri.l);
        assertEquals(4, ri.r);
        assertEquals(4, ri.nextmark);
        assertFalse(ri.isDone4Mark());

        /**
         * =================================================<br>
         * B---- --END
         */
        br.load();
        assertEquals(30, br.readed);
        mode = br.mark(boundary);
        assertEquals(35, br.readed);
        assertEquals(MarkMode.FOUND, mode);
        // ITEM
        ri = br.item;
        assertTrue(ri.isLoaded);
        assertEquals(4, ri.l);
        assertEquals(4, ri.r);
        assertEquals(5, ri.nextmark);
        assertTrue(ri.isDone4Mark());
        // ITEM.NEXT
        ri = br.item.next;
        assertTrue(ri.isLoaded);
        assertEquals(2, ri.l);
        assertEquals(2, ri.r);
        assertEquals(2, ri.nextmark);
        assertFalse(ri.isDone4Mark());
        // ITEM.NEXT.NEXT
        ri = br.item.next.next;
        assertFalse(ri.isLoaded);

        s = br.dumpAsString();
        assertEquals('-', (char) br.item.buffer[0]);
        assertEquals("", s);

        /**
         * =================================================<br>
         * --END L--
         */
        br.load();
        assertEquals(35, br.readed);
        mode = br.mark(boundary);
        assertEquals(38, br.readed);
        assertTrue(br.item.next.isStreamEnd);
        // ITEM
        ri = br.item;
        assertTrue(ri.isLoaded);
        assertEquals(2, ri.l);
        assertEquals(5, ri.r);
        assertEquals(5, ri.nextmark);
        assertTrue(ri.isDone4Mark());
        // ITEM.NEXT
        ri = br.item.next;
        assertTrue(ri.isLoaded);
        assertEquals(0, ri.l);
        assertEquals(3, ri.r);
        assertEquals(3, ri.nextmark);
        assertTrue(ri.isDone4Mark());

        assertEquals(MarkMode.STREAM_END, mode);
        s = br.dumpAsString();
        assertEquals("ENDL--", s);

    }

    @Test
    public void test_by_buffer() throws IOException {
        RemountBytes boundary = RemountBytes.create("-----");
        String str = "-----";
        str += "ABCDE";
        str += "-----";
        str += "12345";
        str += "-----";
        str += "RR";
        InputStream ins = Lang.ins(str);
        BufferRing br = new BufferRing(ins, 3, 5);
        String s;
        MarkMode mode;

        br.load();
        mode = br.mark(boundary);
        assertEquals(MarkMode.FOUND, mode);
        br.skipMark();

        br.load();
        mode = br.mark(boundary);
        assertEquals(MarkMode.FOUND, mode);
        s = br.dumpAsString();
        assertEquals("ABCDE", s);

        br.load();
        mode = br.mark(boundary);
        assertEquals(MarkMode.FOUND, mode);
        s = br.dumpAsString();
        assertEquals("12345", s);

        br.load();
        mode = br.mark(boundary);
        assertEquals(MarkMode.STREAM_END, mode);
        s = br.dumpAsString();
        assertEquals("RR", s);

        assertEquals(27, br.readed);
    }

    @Test
    public void test_by_buffer2() throws IOException {
        RemountBytes boundary = RemountBytes.create("-----");
        String str = "-----";
        str += "ABCDE";
        str += "-----";
        str += "12345";
        str += "-----";
        InputStream ins = Lang.ins(str);
        BufferRing br = new BufferRing(ins, 3, 5);
        String s;
        MarkMode mode;

        br.load();
        mode = br.mark(boundary);
        assertEquals(MarkMode.FOUND, mode);
        br.skipMark();

        br.load();
        mode = br.mark(boundary);
        assertEquals(MarkMode.FOUND, mode);
        s = br.dumpAsString();
        assertEquals("ABCDE", s);

        br.load();
        mode = br.mark(boundary);
        assertEquals(MarkMode.FOUND, mode);
        s = br.dumpAsString();
        assertEquals("12345", s);

        br.load();
        mode = br.mark(boundary);
        assertEquals(MarkMode.STREAM_END, mode);
        s = br.dumpAsString();
        assertEquals("", s);

        assertEquals(25, br.readed);
    }

    @Test
    public void test_mark_dangerous_char() throws IOException {
        RemountBytes boundary = RemountBytes.create("**!");
        String str = "-***!";
        InputStream ins = Lang.ins(str);
        BufferRing br = new BufferRing(ins, 3, 5);
        String s;
        MarkMode mode;
        br.load();
        mode = br.mark(boundary);
        assertEquals(MarkMode.FOUND, mode);
        s = br.dumpAsString();
        assertEquals("-*", s);
    }
    
    @Test
    public void test_mark_dangerous_char2() throws IOException {
        RemountBytes boundary = RemountBytes.create("*!");
        String str = "-**!ABC";
        InputStream ins = Lang.ins(str);
        BufferRing br = new BufferRing(ins, 3, 10);
        String s;
        MarkMode mode;
        br.load();
        mode = br.mark(boundary);
        assertEquals(MarkMode.FOUND, mode);
        s = br.dumpAsString();
        assertEquals("-*", s);
    }

    @Test
    public void test_mark_dangerous_char3() throws IOException {
        RemountBytes boundary = RemountBytes.create("**!");
        String str = "---**";
        str += "!--";
        InputStream ins = Lang.ins(str);
        BufferRing br = new BufferRing(ins, 3, 5);
        String s;
        MarkMode mode;
        br.load();
        
        mode = br.mark(boundary);
        assertEquals(MarkMode.FOUND, mode);
        s = br.dumpAsString();
        assertEquals("---", s);
        
        mode = br.mark(boundary);
        assertEquals(MarkMode.STREAM_END, mode);
        s = br.dumpAsString();
        assertEquals("--", s);
    }
}
