package org.nutz.mvc.upload.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.lang.Lang;

public class RemountBytesTest {

    private static RemountBytes RB(String s) {
        return RemountBytes.create(Lang.toBytes(s.toCharArray()));
    }

    @Test
    public void test_case_A() {
        RemountBytes rb = RB("AABC");
        assertEquals(0, rb.fails[0]);
        assertEquals(0, rb.fails[1]);
        assertEquals(1, rb.fails[2]);
        assertEquals(0, rb.fails[3]);
    }

    @Test
    public void test_case_A2() {
        RemountBytes rb = RB("AAABC");
        assertEquals(0, rb.fails[0]);
        assertEquals(0, rb.fails[1]);
        assertEquals(1, rb.fails[2]);
        assertEquals(2, rb.fails[3]);
        assertEquals(0, rb.fails[4]);
    }

    @Test
    public void test_case_B() {
        RemountBytes rb = RB("ABABX");
        assertEquals(0, rb.fails[0]); // A
        assertEquals(0, rb.fails[1]); // B
        assertEquals(0, rb.fails[2]); // A
        assertEquals(1, rb.fails[3]); // B
        assertEquals(2, rb.fails[4]); // X
    }

    @Test
    public void test_case_C() {
        RemountBytes rb = RB("ABCABCX");
        assertEquals(0, rb.fails[0]); // A
        assertEquals(0, rb.fails[1]); // B
        assertEquals(0, rb.fails[2]); // C
        assertEquals(0, rb.fails[3]); // A
        assertEquals(1, rb.fails[4]); // B
        assertEquals(2, rb.fails[5]); // C
        assertEquals(3, rb.fails[6]); // X
    }

    @Test
    public void test_case_D() {
        RemountBytes rb = RB("AAAAAD");
        assertEquals(0, rb.fails[0]); // A
        assertEquals(0, rb.fails[1]); // A
        assertEquals(1, rb.fails[2]); // A
        assertEquals(2, rb.fails[3]); // A
        assertEquals(3, rb.fails[4]); // A
        assertEquals(4, rb.fails[5]); // D
    }

}
